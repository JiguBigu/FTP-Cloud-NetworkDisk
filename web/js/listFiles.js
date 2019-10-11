function listFiles(){
    $.ajax({
        type: "post",
        url: "/Check",    //listFiles的模块
        data: {path: "G:\\"},      //请求路径为根目录（相应用户的根目录，后端代码可识别）
        dataType: "json",
        success: function(data){
            //遍历json数组
            for(var i in data){
                var tr = "<tr>" +
                            '<th><a  href="javascript:void(0);" onclick="downloadFile(\'' + data[i].path + '\')">' + data[i].name + "</a></th>" +
                            "<td>" + data[i].type + "</td>" +
                            "<td>" + data[i].fileSize + "</td>" +
                            "<td>" + data[i].time + "</td>" +
                            "<td style=\"display: none\">" + data[i].path + "</td>" +
                         "</tr>";
                console.log(tr);
                $("#fileRow").append(tr);
            }
        },
        error : function(errorMsg) {
            console.log("获取列表失败");
            alert("获取列表失败");
        }
    })
}

