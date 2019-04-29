/**
 *
 */
var rootUrl = "/java_s04/api/v1.1/employees";

if(location.search){
	login();
}else{
	beforeLogin();
}





function login(){
	var param = decodeURI(location.search.substring(1));
	param = param.split("&");
	var empId = param[0].split("=")[1]
	var name = param[1].split("=")[1];

	$('#hello').html("<h2>ようこそ、" + name + "(" + empId + ")さん</h2>");

	$('#forLogout').html("<input type='button' id='logout' value='ログアウト'>");

	$('#logout').click(logout);

}


function logout(){

	$.ajax({
		type : "GET",
		url : rootUrl + "/logout",
		dataType : "json"

	}).then(function() {
		alert("ログアウトしました。");
		window.location.href = "./index.html";

	}, function() {

		alert("ログアウトに失敗しました。");

	})


}


function beforeLogin(){

	var html = "<a href='Login.html'>ログインページへ</a>";
	$('#forLogin').html(html);

}
