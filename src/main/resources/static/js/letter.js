$(function () {
    $("#sendBtn").click(send_letter);
    $(".close").click(delete_msg);
});

function send_letter() {
    $("#sendModal").modal("hide");
    //发送AJAX请求之前,将CSRF令牌设置到请求的消息头中.
    var token = $("meta[name='_csrf']").attr("content");
    var header = $("meta[name='_csrf_header']").attr("content");
    $(document).ajaxSend(function (e, xhr, options) {
        xhr.setRequestHeader(header, token);
    });

    var toName = $("#recipient-name").val();
    var content = $("#message-text").val();
    $.post(
        CONTEXT_PATH + "/letter/send",
        {"toName": toName, "content": content},
        function (data) {
            data = $.parseJSON(data);
            if (data.code == 200) {
                $("#hintBody").text("发送成功!");
            } else {
                $("#hintBody").text(data.msg);
            }

            $("#hintModal").modal("show");
            setTimeout(function () {
                $("#hintModal").modal("hide");
                location.reload();
            }, 2000);
        }
    );
}

function delete_msg() {
    // // TODO 删除数据
    // $(this).parents(".media").remove();
    $(this).parents(".media").remove();
    //$("#sendModal").modal("hide");
    //发送AJAX请求之前,将CSRF令牌设置到请求的消息头中.
    var token = $("meta[name='_csrf']").attr("content");
    var header = $("meta[name='_csrf_header']").attr("content");
    $(document).ajaxSend(function (e, xhr, options) {
        xhr.setRequestHeader(header, token);
    });
    var letterId = $("#letterId").val();
    console.log("letterId:" + letterId)
    $.post(
        CONTEXT_PATH + "/letter/delete",
        {"letterId": letterId},
        function (data) {
            // data = $.parseJSON(data);
            // if(data.code == 200) {
            //     $("#hintBody").text("删除成功!");
            // } else {
            //     $("#hintBody").text(data.msg);
            // }
            //
            // $("#hintModal").modal("show");
            // setTimeout(function(){
            //     $("#hintModal").modal("hide");
            //     //location.reload();
            // }, 2000);
        }
    );


}