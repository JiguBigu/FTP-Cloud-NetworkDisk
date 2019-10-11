function downloadFile(path){

    var fileName = path.substr(path.lastIndexOf("/") + 1);
    
    downloadPath = $("#defaultDir").attr("placeholder") + fileName;

    console.log("download path: " + fileName);

    /**
     * 获取传输设置
     */
    var option = document.getElementById("option").value;

    $.ajax({
        type: "post",
        url: "/Download",
        async: false,
        data: {"serverPath":path,  "userPath": downloadPath, "option":option},  //userPath需要定义为用户要存的目录
        dataType: "json",
        success: function(data){
            for(var i = 1; i <= 100; i++){
                document.getElementById("bar").style.width = i + "%";
            }
            alert("下载成功")
            document.getElementById("bar").style.width = 0 +"%";
        },
        error: function(){
            alert("下载失败");
        }
    })

    console.log("user's path: " + downloadPath);
    console.log("server path" + path);
}