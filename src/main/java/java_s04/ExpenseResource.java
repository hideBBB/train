package java_s04;

import javax.ws.rs.Path;

import dao.ExpenseDAO;

/**
 * 経費関連のサービス実装。
 */
@Path("expenses")
public class ExpenseResource {
	private final ExpenseDAO expDao = new ExpenseDAO();



}
