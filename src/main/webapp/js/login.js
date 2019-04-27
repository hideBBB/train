/**
 *
 */


var rootUrl = "/java_s04/api/v1.1/employees";


$('#login').click(login);





/**
 * 社員IDとパスワードをサーバーに送り、照合する
 * 照合成功した場合はトップページに遷移
 * 照合失敗した場合は遷移しない
 * Employee型がjson形式で返ってくる
 */
function login(){

	var empId = $('#empId').val();
	var pass = $('#pass').val();

//	var formdata = new FormData();
//	formdata.append("empId",empId);
//	formdata.append("pass",pass);
//
//	var id = $('#empId').val();

	var requestQuery = {
			empId:empId,
			pass:pass
	}

//	var fd = new FormData(document.getElementById("loginForm"));
//
//
//	console.log(fd.get("empId"));
//	console.log(fd.get("pass"));

	$.ajax({
		type : "GET",
		url : rootUrl+"/login?empId="+ empId +"&pass=" + pass,
		dataType : "json",
//		data:fd,
//		data:formdata,
//		data:requestQuery,
		contentType : false,
		processData : false


	}).then(
			function(json){
//				if(json != null){
//					console.log(json);
//					var url = "../index.html?empId="+ json.empId +"&name=" + json.name;
//					window.location.href(url);
//				}else{
//					alert("社員IDかパスワードが間違っています");
//				}

				console.log(json);


			},
			function(){

				console.log("バカ");


			}
	)



}