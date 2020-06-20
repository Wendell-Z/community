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
    /**
     * 定义日志组件
     */
    private static final Logger logger = LoggerFactory.getLogger(SensitiveFilter.class);
    /**
     * 定义敏感词的替换词 每个敏感词均替换为一个***
     */
    private static final String REPLACE_WORD = "***";

    /**
     * 定义根节点 前缀树的根节点为空
     */
    private final TrieNode rootNode = new TrieNode();

    /**
     * 定义前缀树的数据结构
     */
    private class TrieNode {
        /**
         * 标记当前节点是否为单词的结尾
         * 默认不是结尾 即false
         * 往树中添加敏感词时遍历到结尾会将该值置为true
         */
        private boolean isWordEnd = false;
        /**
         * 定义子节点 子节点可能有多个 因此用map作为数据结构
         * key：子节点的字符 value：子节点的指针
         */
        private Map<Character, TrieNode> subNode = new HashMap<>();

        /**
         * 查看当前节点是否是敏感词的结尾
         *
         * @return
         */
        public boolean isWordEnd() {
            return this.isWordEnd;
        }

        /**
         * 将当前节点置为敏感词的结尾
         *
         * @param isWordEnd
         */
        public void setWordEnd(boolean isWordEnd) {
            this.isWordEnd = isWordEnd;
        }

        /**
         * 根据字符获取当前节点的子节点
         *
         * @param c
         * @return
         */
        public TrieNode getSubNode(Character c) {
            return this.subNode.get(c);
        }

        /**
         * 给当前节点添加子节点
         *
         * @param c
         * @param subNode
         */
        public void addSubNode(Character c, TrieNode subNode) {
            this.subNode.put(c, subNode);
        }
    }

    /**
     * 首先在类加载实例化后初始化敏感词文件，即将敏感词文件中的所有敏感词构造到前缀树中。
     */
    @PostConstruct
    public void init() {
        try (
                //获取当前类加载的位置 即/target/classes下
                //sensitive-words.txt application.properties等配置文件在经过maven编译后都在classes目录下
                //定义输入流
                InputStream in = this.getClass().getClassLoader().getResourceAsStream("sensitive-words.txt");
                //利用缓存读取
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        ) {
            //定义敏感词
            String keyword;
            //敏感词在文件中是一行一行书写的
            //当读到的keyword不为空，就添加到树中
            while ((keyword = reader.readLine()) != null) {
                //将敏感词初始化到树中
                this.addKeyWord(keyword);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 将敏感词构造到前缀树中
     *
     * @param keyword 敏感词文件中的敏感词
     */
    private void addKeyWord(String keyword) {
        //设置一个临时节点 作为指向当前根节点的指针
        //每次构造最开始 tempNode为根节点
        TrieNode tempNode = this.rootNode;

        //遍历敏感词的每一个字符 判断树中是否已经存在
        for (int i = 0; i < keyword.length(); i++) {
            Character c = keyword.charAt(i);

            //查看当前节点的子节点中是否有该字符
            TrieNode subNode = tempNode.getSubNode(c);
            //如果没有 给当前节点添加一个子节点
            if (subNode == null) {
                //当前子节点为空 新添加一个子节点
                subNode = new TrieNode();
                tempNode.addSubNode(c, subNode);
            }

            //有子节点 则将根节点置为该子节点 准备遍历下一个字符
            tempNode = subNode;
            //如果当前字符是该敏感词的最后一个字符 将wordEnd标记置为true
            //执行完if中的语句后 一个敏感词就构造到前缀树中了
            if (i == keyword.length() - 1) {
                tempNode.setWordEnd(true);
            }
        }

    }

    /**
     * 传入字符串，在前缀树中遍历字符串的字符，若遍历过程中找到了目标字符串的敏感词，将其替换为***
     *
     * @param text 要过滤的目标字符串
     * @return
     */
    public String sensitiveWordFilter(String text) {
        //若是空字符串 返回空
        if (StringUtils.isBlank(text)) {
            return null;
        }

        // 根节点
        // 每次在目标字符串中找到一个敏感词，完成替换之后，都要再次从根节点遍历树开始一次新的过滤
        TrieNode tempNode = rootNode;
        // begin指针作用是目标字符串每次过滤的开头
        int begin = 0;
        // position指针的作用是指向待过滤的字符
        // 若position指向的字符是敏感词的结尾，那么text.subString(begin,position+1)就是一个敏感词
        int position = 0;
        //过滤后的结果
        StringBuilder result = new StringBuilder();

        //开始遍历 position移动到目标字符串尾部是 循环结束
        while (position < text.length()) {
            // 最开始时begin指向0 是第一次过滤的开始
            // position也是0
            char c = text.charAt(position);

            //忽略用户故意输入的符号 例如嫖※娼 忽略※后 前后字符其实也是敏感词
            if (isSymbol(c)) {
                //判断当前节点是否为根节点
                //若是根节点 则代表目标字符串第一次过滤或者目标字符串中已经被遍历了一部分
                //因为每次过滤掉一个敏感词时，都要将tempNode重新置为根节点,以重新去前缀树中继续过滤目标字符串剩下的部分
                //因此若是根节点，代表依次新的过滤刚开始，可以直接将该特殊符号字符放入到结果字符串中
                if (tempNode == rootNode) {
                    //将用户输入的符号添加到result中
                    result.append(c);
                    //此时将单词begin指针向后移动一位，以开始新的一个单词过滤
                    begin++;
                }
                //若当前节点不是根节点，那说明符号字符后的字符还需要继续过滤
                //所以单词开头位begin不变化，position向后移动一位继续过滤
                position++;
                continue;
            }
            //判断当前节点的子节点是否有目标字符c
            tempNode = tempNode.getSubNode(c);
            //如果没有 代表当前beigin-position之间的字符串不是敏感词
            // 但begin+1-position却不一定不是敏感词
            if (tempNode == null) {
                //所以只将begin指向的字符放入过滤结果
                result.append(text.charAt(begin));
                //position和begin都指向begin+1
                position = ++begin;
                //再次过滤
                tempNode = rootNode;
            } else if (tempNode.isWordEnd()) {
                //如果找到了子节点且子节点是敏感词的结尾
                //则当前begin-position间的字符串是敏感词 将敏感词替换掉
                result.append(REPLACE_WORD);
                //begin移动到敏感词的下一位
                begin = ++position;
                //再次过滤
                tempNode = rootNode;
                //&& begin < position - 1
            } else if (position + 1 == text.length()) {
                //特殊情况
                //虽然position指向的字符在树中存在，但不是敏感词结尾，并且position到了目标字符串末尾（这个重要）
                //因此begin-position之间的字符串不是敏感词 但begin+1-position之间的不一定不是敏感词
                //所以只将begin指向的字符放入过滤结果
                result.append(text.charAt(begin));
                //position和begin都指向begin+1
                position = ++begin;
                //再次过滤
                tempNode = rootNode;
            } else {
                //position指向的字符在树中存在，但不是敏感词结尾，并且position没有到目标字符串末尾
                position++;
            }
        }
        return begin < text.length() ? result.append(text.substring(begin)).toString() : result.toString();
    }

    /**
     * 判断是否为特殊符号
     *
     * @param c
     * @return
     */
    private boolean isSymbol(Character c) {
        // 0x2E80~0x9FFF 是东亚文字范围
        return !CharUtils.isAsciiAlphanumeric(c) && (c < 0x2E80 || c > 0x9FFF);
    }

}
