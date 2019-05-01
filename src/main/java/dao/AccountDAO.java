package dao;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import beans.Account;



public class AccountDAO {

	private static final String LOGIN_QUERY = "SELECT A.ID,A.AUTHORITY FROM ACCOUNT A WHERE A.EMPID=? AND A.PASS=?";

	/**
	 * アカウントテーブルからEMPID指定の検索を実施してパスワードがあっているか照合する
	 *
	 * @param empId 検索対象のEMPID
	 * @param pass 検索対象のPASS
	 * @return パスワードがあっていた場合、対象のidとauthが入ったAccount型を返す。失敗した場合はnullが返る。
	 */
	public Account login(String empId,String pass){
		Account result = null;

		Connection connection = ConnectionProvider.getConnection();
		if (connection == null) {
			return result;
		}

		try (PreparedStatement statement = connection.prepareStatement(LOGIN_QUERY)) {
			statement.setString(1, empId);
			statement.setString(2, pass);

			ResultSet rs = statement.executeQuery();

			if (rs.next()) {
				result = new Account();
				result.setId(rs.getInt("ID"));
				result.setAuth(rs.getString("AUTHORITY"));

			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			ConnectionProvider.close(connection);
		}


		return result;
	}

}
