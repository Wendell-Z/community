package com.nowcoder.community.util;

import org.apache.commons.lang3.CharUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

@Component
public class SensitiveFilter {
    private static final Logger logger = LoggerFactory.getLogger(SensitiveFilter.class);

    // 替换符
    private static final String REPLACEMENT = "***";

    // 根节点
    private TrieNode rootNode = new TrieNode();

    /**
     * 在类加载后初始化敏感词表
     */
    @PostConstruct
    public void init() {
        try (
                //定义输入流
                InputStream is = this.getClass().getClassLoader().getResourceAsStream("sensitive-words.txt");
                //缓存读取
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        ) {
            String keyword;
            //敏感词是一行一行的
            while ((keyword = reader.readLine()) != null) {
                // 每遍历到一个敏感词 添加到前缀树
                this.addKeyword(keyword);
            }
        } catch (IOException e) {
            logger.error("加载敏感词文件失败: " + e.getMessage());
        }
    }

    /**
     * 根据敏感词表构造前缀树
     *
     * @param keyWord
     */
    private void addKeyword(String keyWord) {
        //设置一个临时节点 用来做指针
        //每次构造最开始 temNode为根节点
        TrieNode tmpNode = this.rootNode;
        //对该字符串的字符进行遍历
        for (int i = 0; i < keyWord.length(); i++) {
            Character c = keyWord.charAt(i);
            //从父节点中获取子节点
            TrieNode subNode = tmpNode.getSubNode(c);
            //若子节点不存在  新建一个子节点 添加
            if (subNode == null) {
                subNode = new TrieNode();
                tmpNode.addSubNode(c, subNode);
            }
            //若存在 则节点指针向下
            tmpNode = subNode;
            //若当前字符是最后一个字符 那么该节点置为true 即敏感词的最后一位
            if (i == keyWord.length() - 1) {
                tmpNode.setKeyWordEnd(true);
            }
        }
    }


    public String sensitiveWordFilter(String text) {
        if (StringUtils.isBlank(text)) {
            return null;
        }

        // 指针1
        TrieNode tempNode = rootNode;
        // 指针2
        int begin = 0;
        // 指针3
        int position = 0;
        // 结果
        StringBuilder sb = new StringBuilder();

        while (position < text.length()) {
            char c = text.charAt(position);

            // 跳过符号
            if (isSymbol(c)) {
                // 若指针1处于根节点,将此符号计入结果,让指针2向下走一步
                if (tempNode == rootNode) {
                    sb.append(c);
                    begin++;
                }
                // 无论符号在开头或中间,指针3都向下走一步
                position++;
                continue;
            }

            // 检查下级节点
            tempNode = tempNode.getSubNode(c);
            if (tempNode == null) {
                // 以begin开头的字符串不是敏感词
                sb.append(text.charAt(begin));
                // 进入下一个位置
                position = ++begin;
                // 重新指向根节点
                tempNode = rootNode;
            } else if (tempNode.isKeyWordEnd()) {
                // 发现敏感词,将begin~position字符串替换掉
                sb.append(REPLACEMENT);
                // 进入下一个位置
                begin = ++position;
                // 重新指向根节点
                tempNode = rootNode;
            } else if (position + 1 == text.length() && begin < position - 1) {
                // 当position指向字符串最后一位字符且该字符串非敏感字符的最后一位时，即当前begin到position之间不是敏感字符
                // 此时应判断begin与position的距离
                //若 position -begin > 1 说明begin和position之间还有若干字符没有判断它们是否属于敏感词
                //此时应position = begin++再次进行过滤
                sb.append(text.charAt(begin));
                position = ++begin;
                tempNode = rootNode;
            } else {
                // 检查下一个字符
                position++;
            }
        }
        // 将最后一批字符计入结果
        sb.append(text.substring(begin));
        return sb.toString();
    }

    // 判断是否为符号
    private boolean isSymbol(Character c) {
        // 0x2E80~0x9FFF 是东亚文字范围
        return !CharUtils.isAsciiAlphanumeric(c) && (c < 0x2E80 || c > 0x9FFF);
    }

    /**
     * 前缀树数据结构
     */
    private class TrieNode {

        private boolean isKeyWordEnd = false;
        //子节点
        Map<Character, TrieNode> subNode = new HashMap<>();

        //查看当前子节点是否是末节点
        public boolean isKeyWordEnd() {
            return isKeyWordEnd;
        }

        //设置当前节点为末节点
        public void setKeyWordEnd(boolean keyWordEnd) {
            isKeyWordEnd = keyWordEnd;
        }

        //获取当前节点的子节点
        public TrieNode getSubNode(Character c) {
            return subNode.get(c);
        }

        //添加子节点
        public void addSubNode(Character c, TrieNode node) {
            this.subNode.put(c, node);
        }

    }

}
