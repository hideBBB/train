package dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import beans.Expense;

/**
 * 経費データを扱うDAO
 */
public class ExpenseDAO {
	/**
	 * クエリ文字列
	 */
	public static final String SELECT_ALL_QUERY = "SELECT * FROM EXPENSE EX ORDER BY EX.ID";
	public static final String SELECT_BY_EMPID_QUERY = "SELECT * FROM EXPENSE EX WHERE 1=1 AND EX.REQEMPID = ?";
	public static final String SELECT_BY_ID_QUERY = "SELECT * FROM EXPENSE EX WHERE 1=1 AND EX.ID = ?";
	public static final String UPDATE_BY_ID_QUERY = "UPDATE EXPENSE E SET E.UP_DATE=?, E.UP_EMPID=?, E.STATUS=? WHERE E.ID=?";



	/**
	 * 権限がadminの場合、全件検索を実施する。userの場合、id指定検索を実行する。
	 *
	 * @param id ログイン中の従業員ID。
	 * @param auth ログイン中の従業員の権限（adminかuser）
	 * @return 検索結果を収めたList。検索結果が存在しない場合は長さ0のリストが返る。
	 */
	public List<Expense> findByAuth(int id,String auth){
		List<Expense> result = new ArrayList<>();

		Connection connection = ConnectionProvider.getConnection();
		if (connection == null) {
			return result;
		}

		String queryString = SELECT_ALL_QUERY;
		if("user".equals(auth)){
			queryString = SELECT_BY_EMPID_QUERY;
		}

		try (PreparedStatement statement = connection.prepareStatement(queryString)) {
			if("user".equals(auth)){
				statement.setInt(1, id);
			}

			ResultSet rs = statement.executeQuery();

			while (rs.next()) {
				result.add(processRow(rs));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			ConnectionProvider.close(connection);
		}

		return result;

	}


	/**
	 * 権限がadminの場合、全件検索を実施する。userの場合、id指定検索を実行する。
	 * @param id 経費ID。
	 * @return 検索結果を収めたExpense型。検索結果が存在しない場合はnullが返る。
	 */
	public Expense findById(int id){
		Expense result = null;

		Connection connection = ConnectionProvider.getConnection();
		if (connection == null) {
			return result;
		}

		String queryString = SELECT_BY_ID_QUERY;

		try (PreparedStatement statement = connection.prepareStatement(queryString)) {
			statement.setInt(1, id);

			ResultSet rs = statement.executeQuery();

			if (rs.next()) {
				result = processRow(rs);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			ConnectionProvider.close(connection);
		}

		return result;

	}


	/**
	 * 申請のステータスを更新する
	 * @param id 経費ID。
	 * @param status 承認済 or 却下理由
	 * @param up_EmpId 更新者ID
	 * @return 更新が成功したときにはOKがString型で返る
	 */
	public String update(int up_EmpId,String status,int id){
		String result = "NG";

		Connection connection = ConnectionProvider.getConnection();
		if (connection == null) {
			return result;
		}

		String queryString = UPDATE_BY_ID_QUERY;

		try (PreparedStatement statement = connection.prepareStatement(queryString)) {
			statement.setString(1, calendar());
			statement.setInt(2, up_EmpId);
			statement.setString(3, status);
			statement.setInt(4, id);


			statement.executeUpdate();

			result = "OK";

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			ConnectionProvider.close(connection);
		}

		return result;

	}








	/**
	 * 検索結果からオブジェクトを復元する。
	 *
	 * @param rs 検索結果が収められているResultSet。rs.next()がtrueであることが前提。
	 * @return 検索結果を収めたオブジェクト
	 * @throws SQLException 検索結果取得中に何らかの問題が発生した場合に送出される。
	 */
	private Expense processRow(ResultSet rs) throws SQLException {
		Expense result = new Expense();

		// Expense本体の再現
		result.setId(rs.getInt("ID"));
		Date reqDate = rs.getDate("REQDATE");
		if (reqDate != null) {
			result.setReqDate(reqDate.toString());
		}
		Date up_Date = rs.getDate("UP_DATE");
		if (up_Date != null) {
			result.setUp_Date(up_Date.toString());
		}
		result.setReqEmpId(rs.getInt("REQEMPID"));
		result.setUp_EmpId(rs.getInt("UP_EMPID"));
		result.setTitle(rs.getString("TITLE"));
		result.setPayDest(rs.getString("PAYDESTINATION"));
		result.setAmount(rs.getInt("AMOUNT"));
		result.setStatus(rs.getString("STATUS"));

		return result;
	}





	/**
	 * 現在日付を整えて返す
	 * @return 日付（2019-04-01）
	 */
	private String calendar(){
		Calendar cal = Calendar.getInstance();
		String year = String.valueOf(cal.get(Calendar.YEAR));
		String month = String.valueOf(cal.get(Calendar.MONTH)+1); //月は「一月」が0であらわされる
		String date = String.valueOf(cal.get(Calendar.DATE));

		//月と日付が一桁なら先頭に0をつける
		month = formatDate(month);
		date = formatDate(date);

		return year+"-"+month+"-"+date;


	}

	private String formatDate(String num){
		if(num.length() == 1){
			num = "0"+num;
		}
		return num;
	}










}
