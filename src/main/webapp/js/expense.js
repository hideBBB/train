/**
 *
 */
var rootUrl = "/java_s04/api/v1.1/expenses";

initPage();

function initPage(){

findByAuth();

}


function findByAuth(){
	console.log('findByAuth start.')
	$.ajax({
		type : "GET",
		url : rootUrl,
		dataType : "json",
		success : function(json){
			console.log(json);
		}
//		success : renderTable
	});
}


function renderTable(data) {
	var headerRow = '<tr><th>社員ID</th><th>氏名</th></tr>';

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
			row.append($('<td>').text(expense.reqEmpId));
			row.append($('<td>').text(expense.title));
			row.append($('<td>').text(expense.amount));
			row.append($('<td>').text(expense.status));
//			row.append($('<td>').append(
//					$('<button>').text("詳細").attr("type","button").attr("onclick", "findById("+expense.id+')')
//				));
//			row.append($('<td>').append(
//					$('<button>').text("削除").attr("type","button").attr("onclick", "deleteById("+employee.id+')')
//				));
			table.append(row);
		});

		$('#expenses').append(table);
	}
}



