var verSuccess = false;
function register() {
    var username = document.getElementById("username").value;
    var password = document.getElementById("password").value;
    console.log("username:" + username);
    console.log("password:" + password);
    console.log("verification2:" + verSuccess);
    if(verSuccess){
        verRegister();
    } else {
        alert("请拖动滑块证明你不是机器人");
    }
}

function verRegister() {
    var username = document.getElementById("username").value;
    var password = document.getElementById("password").value;
    $.ajax({
        type : 'post',	//传输类型
        async : false,	//同步执行
        url : '/Register',	//web.xml中注册的Servlet的url-pattern
        data : { "username":username, "password":password},
        dataType : 'json', //返回数据形式为json
        success : function(registerSuccess) {
            if (registerSuccess[0] == false) {
                alert("用户名已存在，请重新输入用户名")
            } else {
                alert("注册成功，请前往登录！")
                window.location.href="login.html";
            }
        },
        error : function(errorMsg) {
            console.log("Register Erro !");
        }
    })
}