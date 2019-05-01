package java_s04;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import beans.Expense;
import dao.ExpenseDAO;

/**
 * 経費関連のサービス実装。
 */
@Path("expenses")
public class ExpenseResource {
	private final ExpenseDAO expDao = new ExpenseDAO();

	/**
	 * セッションに入っている場合はログインデータを使って経費データ一覧を取得する
	 * セッションのAccountからauthを取得
	 * 権限がadminの場合は全リスト、userの場合は自分の申請のみ取得
	 * @return ログインしていた場合は経費情報（Expense型）をJSON形式で返す。失敗した場合は空のオブジェクトが返る
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Expense findByAuth(@Context HttpServletRequest request){
		Expense result = null;

        HttpSession session = request.getSession(false);
        String accountInfo = session.getAttribute("Account").toString();
        String employeeInfo = session.getAttribute("Employee").toString();

        System.out.println(accountInfo);
        System.out.println(employeeInfo);





		return result;

	}


}
