function displsyUpload(filename, type, size, time){
    var tr = "<tr>" +
                            "<th>" + filename + "</th>" +
                            "<td>" + type + "</td>" +
                            "<td>" + size + "</td>" +
                            "<td>" + time + "</td>" +
                          "</tr>";
                console.log(tr);
                $("#fileRow").append(tr);
}

function upload(){
    $("#selectFile").click();

    $("#selectFile").change(function(e){
        var uploadData = new FormData();
        //获取已选择文件
        var file = document.getElementById("selectFile").files[0];
        var filename = $("#selectFile").val();
        var type, size, time,
            progress = "未上传", uploadVal = "开始上传";

        if(filename != ""){
            type = file.type;
            size = file.size;
            time = file.time;

            displsyUpload(filename, type, size, time);
        }

        //将文件添加进formData中进行传输
         uploadData.append("serverPath", "/");

        uploadData.append("option", document.getElementById("option").value);
    
        uploadData.append("file", file);

        var option = document.getElementById("option").value;
        console.log("option:" + option);

        //传输路径
        $.ajax({
            processData: false,//这个必须有，不然会报错
            contentType: false,//这个必须有，不然会报错
            type: "post",
            async : false,	//同步执行
            url: "/Upload",
            data: uploadData,
            dataType: "json",
            success: function(data){
                for(var i = 1; i <= 100; i++){
                    document.getElementById("bar").style.width = i + "%";
                }
                alert("文件上传成功");
                document.getElementById("bar").style.width = 0 + "%";
            },
            error: function(data){
                // alert("文件上传失败");
                console.log("上传文件失败");
            }
        })

    
        //设上传文件的框为空使得下次选择不重复
        $("#selectFile").val("");
    })
};





