/**
 *
 */
var rootUrl = "/java_s04/api/v1.1/employees";

if(location.search){
	login();
}

header();


function header(){
	$.ajax({
		type : "GET",
		url : rootUrl+"/user",
		dataType : "json",
		success : function(data){
			console.log(data);

			if(data[0] != null){
				var userInfo = "従業員ID："+data[0]+", 従業員名："+data[1];

				$('#userInfo').html(userInfo);

				$('#forLogout').html("<input type='button' id='logout' value='ログアウト'>");
				$('#logout').click(logout);
			}else{
				var html = "<input type='button' value='ログインページ' onclick='location.href=`Login.html`'>";
				$('#forLogin').html(html);
			}


		}

	})

}




function login(){
	var param = decodeURI(location.search.substring(1));
	param = param.split("&");
	var empId = param[0].split("=")[1]
	var name = param[1].split("=")[1];

	$('#hello').html("<h2>ようこそ、" + name + "(" + empId + ")さん</h2>");


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


