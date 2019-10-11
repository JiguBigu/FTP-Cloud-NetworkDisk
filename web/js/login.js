function login() {
    var username = document.getElementById("username").value;
    var password = document.getElementById("password").value;
    console.log("username:" + username);
    console.log("password:" + password);
    $.ajax({
        type : 'post',	//传输类型
        async : false,	//同步执行
        url : '/Login',	//web.xml中注册的Servlet的url-pattern
        data : { "username":username, "password":password},
        dataType : 'json', //返回数据形式为json
        success : function(loginSuccess) {
            if (loginSuccess[0] == false) {
                alert("用户名或密码错误");
            }else{
                alert("登陆成功！");
                window.location.href="ftp.html";
            }
        },
        error : function(errorMsg) {
            console.log("Login Erro !");
        }
    });
}
