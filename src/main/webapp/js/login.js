/**
 *
 */


var rootUrl = "/java_s04/api/v1.1/employees";


$('#login').click(login);






//社員IDとパスワードをサーバーに送り、照合する
//照合成功した場合はトップページに遷移
//照合失敗した場合は遷移しない

function login(){

	var empId = $('#empId').val();
	var pass = $('#pass').val();

	var requestQuery = {
			empId:empId,
			pass:pass
	}


	$.ajax({
		type : "POST",
		url : rootUrl+"/login",
		dataType : "json",
		data:requestQuery

	}).then(
			function(json){




			},
			function(){



			}
	)



}