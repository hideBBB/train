/**
 *
 */


if(location.search){
	loginResult();
}


function loginResult(){
	var param = location.search.substring(1);
	param = param.split("&");
	var empId = param[0].split("=")[1]
	var name = param[1].split("=")[1];



	console.log(empId,name);

}