/**
 *
 */
var rootUrl = "/java_s04/api/v1.1/expenses";
var empUrl = "/java_s04/api/v1.1/employees";

initPage();

$('#newRegist').click(registMenu);






function initPage(){

findByAuth();

}


function findByAuth(){
	console.log('findByAuth start.')
	$.ajax({
		type : "GET",
		url : rootUrl,
		dataType : "json",
		success : renderTable
	});
}




function registMenu(){
	console.log('registMenu start.');

	$.ajax({
		type : "GET",
		url : rootUrl+"/numOfRequest",
		dataType : "json",
		success : function(data){
			var registId = data+1;
			var menu = "申請ID : <input type='text' readonly='readonly' value='"+registId+"'><br>" +
					"申請日 : <br>" +
					"申請者 : <br>" +
					"タイトル : <br>" +
					"支払先 : <br>" +
					"金額 : <br>" +
					"ステータス : <input type='text' readonly='readonly' value='申請中'>";





			$('#registMenu').html(menu);
		}



	});

}





function renderTable(data) {
	var headerRow = '<tr><th>申請ID</th><th>申請日</th><th>更新日</th><th>申請者</th><th>タイトル</th><th>金額</th><th>ステータス</th></tr>';

	$('#expenses').children().remove();

	if (data.length === 0) {
		$('#expenses').append('<p>現在データが存在していません。</p>')
	} else {
		var table = $('<table>').attr('border', 1);
		table.append(headerRow);

		//for文みたいな繰り返し
		$.each(data, function(index, expense) {
			var row = $('<tr>');
			row.append($('<td>').text(expense.id));
			row.append($('<td>').text(expense.reqDate));
			row.append($('<td>').text(expense.up_date));

			$.ajax({
				type : "GET",
				url : empUrl + "/" + expense.reqEmpId,
				dataType : "json",
				success : function(data){
					row.append($('<td>').text(data.name));
					row.append($('<td>').text(expense.title));
					row.append($('<td>').text(expense.amount));
					row.append($('<td>').text(expense.status));
//					row.append($('<td>').append(
//							$('<button>').text("詳細").attr("type","button").attr("onclick", "findById("+expense.id+')')
//						));
//					row.append($('<td>').append(
//							$('<button>').text("削除").attr("type","button").attr("onclick", "deleteById("+employee.id+')')
//						));
					table.append(row);

				}
			});

		});

		$('#expenses').append(table);
	}
}



