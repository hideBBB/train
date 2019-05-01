package dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
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
	public static final String SELECT_BY_ID_QUERY = "SELECT * FROM EXPENSE EX WHERE 1=1 AND EX.REQEMPID = ?";


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
			queryString = SELECT_BY_ID_QUERY;
		}

		try (PreparedStatement statement = connection.prepareStatement(queryString)) {
			if("user".equals(auth)){
				statement.setInt(1, id);
			}

			ResultSet rs = statement.executeQuery();

			while (rs.next()) {
				result.add(processRow(rs));
		        System.out.println(rs.getString("TITLE"));

			}
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














}
