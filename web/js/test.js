function getFilePath() {

    $("input[type='file']").on('change', function () {
        var oFReader = new FileReader();
        var file = document.getElementById('xFile').files[0];
        oFReader.readAsDataURL(file);
        oFReader.onloadend = function(oFRevent){
            var src = oFRevent.target.result;
            $('.content').attr('src',src);
            alert(src);
        }
    });

    var userPath = document.getElementById("xFile").value;
    console.log(userPath);
    var test = getPath(document.getElementById("xFile"));
    console.log(test);

    $.ajax({
        type: "post",
        async : false,	//同步执行
        url: "/Upload",
        data: {"userPath": userPath},
        dataType: "json",
        success: function(data){
            alert("文件上传成功");
        },
        error: function(data){
            alert("文件上传失败");
        }
    })
}

function getPath(obj)
{

    if(obj)
    {

        if (window.navigator.userAgent.indexOf("MSIE")>=1)
        {
            obj.select();

            return document.selection.createRange().text;
        }

        else if(window.navigator.userAgent.indexOf("Firefox")>=1)
        {
            if(obj.files)
            {

                return obj.files.item(0).getAsDataURL();
            }
            return obj.value;
        }
        return obj.value;
    }
}
//参数obj为input file对象