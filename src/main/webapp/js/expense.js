/**
 *
 */
var rootUrl = "/java_s04/api/v1.1/expenses";
var empUrl = "/java_s04/api/v1.1/employees";

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
		success : renderTable
	});
}

function findById(id){
	console.log('findById start.')
	$.ajax({
		type : "GET",
		url : rootUrl + "/" + id,
		dataType : "json",
		success : renderDetails
	});
}

function approveUpdate(id){
	console.log(id);

	var fd = new FormData();
	fd.append("status","承認済");

	console.log('approveUpdate start.')
	$.ajax({
		type : "PUT",
		url : rootUrl+"/"+id,
		dataType : "json",
		data : fd,
		contentType : false,
		processData : false,
		success : function(data){
			alert("経費申請を承認しました。");
			renderDetails(data);
		}
	});

}


function rejectUpdate(id){

	var fd = new FormData();
	fd.append("status","却下："+$('#reason').val());

	console.log('rejectUpdate start.')
	$.ajax({
		type : "PUT",
		url : rootUrl+"/"+id,
		dataType : "json",
		data : fd,
		contentType : false,
		processData : false,
		success : function(data){
			alert("経費申請を却下しました。");
			renderDetails(data);
		}
	});


//	console.log($('#reason').val());

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
			row.append($('<td>').text(expense.up_Date));

			$.ajax({
				type : "GET",
				url : empUrl + "/" + expense.reqEmpId,
				dataType : "json",
				async: false,
				success : function(data){
					row.append($('<td>').text(data.name));
					row.append($('<td>').text(expense.title));
					row.append($('<td>').text(expense.amount));
					row.append($('<td>').text(expense.status));
					row.append($('<td>').append(
							$('<button>').text("詳細").attr("type","button").attr("onclick", "findById("+expense.id+')')
						));

					table.append(row);

				}
			});

		});

		$('#expenses').append(table);
	}
}


/**
 * 経費詳細を取得してhtmlに挿入する
 * @param data
 */
function renderDetails(data) {
	var headerRow = '<tr><th>申請ID</th><th>申請日</th><th>更新日</th><th>申請者</th><th>タイトル</th><th>支払先</th><th>金額</th><th>ステータス</th><th>更新者</th></tr>';

	$('#expenseDetail').children().remove();

	var table = $('<table>').attr('border', 1);
	table.append(headerRow);

	var row = $('<tr>');
	row.append($('<td>').text(data.id));
	row.append($('<td>').text(data.reqDate));
	row.append($('<td>').text(data.up_Date));


	//申請者名前の問い合わせ
	$.ajax({
		type : "GET",
		url : empUrl + "/" + data.reqEmpId,
		dataType : "json",
		async : false,
		success : function(json){
			row.append($('<td>').text(json.name));
			row.append($('<td>').text(data.title));
			row.append($('<td>').text(data.payDest));
			row.append($('<td>').text(data.amount));
			row.append($('<td>').text(data.status));
			var buttonFlg = true; //ステータスが申請中の場合はtrue
			if(data.status != "申請中"){
				buttonFlg = false;
			}

			//更新者名前の問い合わせ
			if(data.up_EmpId != 0){
				$.ajax({
					type : "GET",
					url : empUrl + "/" + data.up_EmpId,
					dataType : "json",
					async : false,
					success : function(json){
						row.append($('<td>').text(json.name));
						table.append(row);
						$('#expenseDetail').html(table);
						if(buttonFlg){
							catchAuth(data.id);
						}
					}

				})
			}else{
				row.append($('<td>').text(""));
				table.append(row);
				$('#expenseDetail').html(table);
				if(buttonFlg){
					catchAuth(data.id);
				}
			}

		}
	});


}


//権限がadminの場合のみボタンをセットする
function catchAuth(id){
	var row = $('<div>');
	//権限の問い合わせ
	$.ajax({
		type : "GET",
		url : empUrl + "/auth",
		dataType : "text",
		success : function(data){
			if(data == "admin"){
				row.append($('<button>').text("承認").attr("type","button").attr("onclick","approveUpdate("+id+")"));
				row.append($('<button>').text("却下").attr("type","button").attr("onclick","rejectUpdate("+id+")"));
				row.append($('<br>'));
				row.append($('<textarea>').attr("placeholder","却下理由").attr("rows","5").attr("cols","40").attr("id","reason"));

//				row.append($('<td>').append(
//						$('<button>').text("承認").attr("type","button").attr("onclick", "findById("+expense.id+')')
//					));
				$('#expenseDetail').append(row);

			}

		},
		error : function(){
			console.log("権限を取得できませんでした。");

		}
	});



}
