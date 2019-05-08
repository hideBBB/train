package java_s04;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.glassfish.jersey.media.multipart.FormDataMultiPart;

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
	public List<Expense> findByAuth(@Context HttpServletRequest request){
		List<Expense> result = new ArrayList<>();
        HttpSession session = request.getSession(false);

		//ログインしているかチェック
		if(session == null){
			return result;
		}

        //[0]がアカウントID(従業員IDと同じ)、[1]がアカウント権限
        String[] accountInfo = session.getAttribute("Account").toString().split(",");

        int id = Integer.parseInt(accountInfo[0]);
        String auth = accountInfo[1];

        result = expDao.findByAuth(id, auth);
		return result;

	}

	/**
	 * これまでに申請されている申請数を返す
	 * @return 申請数をint型で返す
	 */
	@GET
	@Path("numOfRequest")
	@Produces(MediaType.APPLICATION_JSON)
	public int findLastId(){
		//全リストを取得して申請数を返す
		return expDao.findByAuth(1, "admin").size();

	}


	/**
	 * 経費IDで経費情報を検索する
	 * @param id
	 * @return Expense型をJSON形式で返す。失敗した場合は空のオブジェクトが返る
	 */
	@GET
	@Path("{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Expense findById(@PathParam("id") int id,@Context HttpServletRequest request){
		Expense result = null;
		HttpSession session = request.getSession(false);

		//ログインしているかチェック
		if(session.equals(null)){
			return result;
		}

		return expDao.findById(id);
	}

	/**
	 * 経費申請の承認・却下を行う
	 * @param id
	 * @return 更新したのち、更新後のExpense型を返す。
	 */
	@PUT
	@Path("{id}")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(MediaType.APPLICATION_JSON)
	public Expense approveUpdate(@PathParam("id") int id,@Context HttpServletRequest request,final FormDataMultiPart form)
			throws WebApplicationException {
		Expense result = null;
		HttpSession session = request.getSession(false);

		//ログインしているかチェック
		if(session == null){
			return result;
		}

		//更新者ID
		int up_EmpId = Integer.parseInt(session.getAttribute("Account").toString().split(",")[0]);
		String status = form.getField("status").getValue();

		expDao.updateStatus(up_EmpId, status, id);


		return expDao.findById(id);
	}


	/**
	 * 経費申請を登録する
	 * Expense型に情報を詰めて、Daoに渡す
	 */
	@POST
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public void newRegist(final FormDataMultiPart form,@Context HttpServletRequest request){
		Expense expense = new Expense();

		HttpSession session = request.getSession(false);

		expense.setId(Integer.parseInt(form.getField("id").getValue()));
		int reqEmpId = Integer.parseInt(session.getAttribute("Employee").toString().split(",")[0]);
		expense.setReqEmpId(reqEmpId);
		expense.setTitle(form.getField("title").getValue());
		expense.setPayDest(form.getField("payDest").getValue());
		int amount = Integer.parseInt(form.getField("amount").getValue());
		expense.setAmount(amount);
		expense.setStatus("申請中");

		expDao.creat(expense);

	}


	/**
	 * 経費を更新する
	 * Expense型に更新情報を詰めて、Daoに渡す
	 * @return 更新したのち、更新後のExpense型を返す。ログインしていなかった場合nullを返す。
	 */
	@POST
	@Path("update")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(MediaType.APPLICATION_JSON)
	public Expense update(final FormDataMultiPart form,@Context HttpServletRequest request){
		Expense result = null;
		Expense expense = new Expense();
		HttpSession session = request.getSession(false);

		//ログインしているかチェック
		if(session == null){
			return result;
		}

		expense.setId(Integer.parseInt(form.getField("id").getValue()));
		expense.setTitle(form.getField("title").getValue());
		expense.setPayDest(form.getField("payDest").getValue());
		int amount = Integer.parseInt(form.getField("amount").getValue());
		expense.setAmount(amount);
		int up_EmpId = Integer.parseInt(session.getAttribute("Employee").toString().split(",")[0]);
		expense.setUp_EmpId(up_EmpId);

		expDao.update(expense);

		result = expDao.findById(Integer.parseInt(form.getField("id").getValue()));

		return result;
	}


}
