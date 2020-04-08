function like(btn, entityType, entityId, entityUserId, postId) {
    var token = $("meta[name='_csrf']").attr("content");
    var header = $("meta[name='_csrf_header']").attr("content");
    $(document).ajaxSend(function (e, xhr, options) {
        xhr.setRequestHeader(header, token);
    });
    $.post(
        CONTEXT_PATH + "/like",
        {"entityType": entityType, "entityId": entityId, "entityUserId": entityUserId, "postId": postId},
        function (data) {
            data = $.parseJSON(data);
            if (data.code == 200) {
                $(btn).children("i").text(data.likeCount);
                $(btn).children("b").text(data.likeStatus == 1 ? '已赞' : "赞");
            } else {
                alert(data.msg);
            }
        }
    );
}

function comment(btn, entityType, entityId) {
    //发送AJAX请求之前,将CSRF令牌设置到请求的消息头中.
    var token = $("meta[name='_csrf']").attr("content");
    var header = $("meta[name='_csrf_header']").attr("content");
    $(document).ajaxSend(function (e, xhr, options) {
        xhr.setRequestHeader(header, token);
    });
    $post(
        CONTEXT_PATH + "/comment/add",
        {"entityType": entityType, "entityId": entityId},
        function (data) {
            data = $.parseJSON(data);
            if (data.code == 200) {
                alert(data.msg);
            } else {
                alert(data.msg);
            }
        }
    );
}