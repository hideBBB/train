package java_s04;

import java.io.InputStream;
import java.sql.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.glassfish.jersey.media.multipart.BodyPartEntity;
import org.glassfish.jersey.media.multipart.ContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;

import beans.Account;
import beans.Employee;
import beans.Gender;
import beans.Photo;
import beans.Post;
import dao.AccountDAO;
import dao.EmployeeDAO;
import dao.Param;
import dao.PhotoDAO;
import dao.PostDAO;

/**
 * 従業員関連のサービス実装。
 * Servlet/JSPの実装とは異なり、画像についてはバイナリでなくpathベースで扱うものとする。
 */
@Path("employees")
public class EmployeeResource {
	private final EmployeeDAO empDao = new EmployeeDAO();
	private final PostDAO postDao = new PostDAO();
	private final PhotoDAO photoDao = new PhotoDAO();
	private final AccountDAO accDao = new AccountDAO();


	/**
	 * 社員IDとパスワードを受け取ってパスワードを照合する
	 * 照合に成功した場合はセッションに情報を保持する
	 * @param empId ログインリクエスト対象の社員ID
	 * @param pass パスワード
	 * @return ログインに成功した場合は従業員情報をJSON形式で返す。失敗した場合は空のオブジェクトが返る。
	 */
	@POST
	@Path("login")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(MediaType.APPLICATION_JSON)
	public Employee login(final FormDataMultiPart form,@Context HttpServletRequest request){
		String empId = form.getField("empId").getValue();
		String pass = form.getField("pass").getValue();

		Employee result = null;
		Account accountInfo = accDao.login(empId, pass);
		if(accountInfo != null){
			result = empDao.findById(accountInfo.getId());
	        HttpSession session = request.getSession();
	        session.setAttribute("Employee",result);
	        //idとauthが入ったAccount型
	        session.setAttribute("Account", accountInfo);

		}else{
			return result;
		}
		return result;
	}


	/**
	 * ログアウトのためにセッションを閉じる
	 * @return ログアウトに成功した場合はokを返す。
	 */
	@GET
	@Path("logout")
	public void logout(@Context HttpServletRequest request){
        HttpSession session = request.getSession(false);
        session.invalidate();
	}

	/**
	 * ログインしているユーザー情報を取得する
	 * @return ログインしているユーザー名、ユーザーIDを返す。ログインしていなかった場合nullが入った配列を返す。
	 */
	@GET
	@Path("user")
	@Produces(MediaType.APPLICATION_JSON)
	public String[] userInfo(@Context HttpServletRequest request){
		String[] result = new String[2];
		HttpSession session = request.getSession(false);

		//ログインしているかチェック
		if(session == null){
			return result;
		}

		//ログインしているユーザー名、ユーザーIDをセット
		result[0] = session.getAttribute("Employee").toString().split(",")[1];
		result[1] = session.getAttribute("Employee").toString().split(",")[2];

        return result;
	}

	/**
	 * ユーザー権限情報の取得
	 * @return ユーザー権限のString型をJSON形式で返す。
	 */
	@GET
	@Path("auth")
	@Produces(MediaType.APPLICATION_JSON)
	public String catchAuth(@Context HttpServletRequest request){
        HttpSession session = request.getSession(false);
        String auth = session.getAttribute("Account").toString().split(",")[1];
        return auth;
	}







	/**
	 * ID指定で従業員情報を取得する。
	 *
	 * @param id 取得対象の従業員のID
	 * @return 取得した従業員情報をJSON形式で返す。データが存在しない場合は空のオブジェクトが返る。
	 */
	@GET
	@Path("{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Employee findById(@PathParam("id") int id) {
		return empDao.findById(id);
	}

	/**
	 * クエリパラメータ指定による検索を実施する。
	 * 何も指定しない場合は全件検索になる。
	 *
	 * @param postId 部署ID。指定しない場合は0が入る。
	 * @param empId ログイン用の従業員ID。指定しない場合はnullが入る。
	 * @param nameParam 名前の一部を指定するためのパラメータ。指定しない場合はnullが入る。
	 * @return 取得した従業員情報のリストをJSON形式で返す。データが存在しない場合は空のオブジェクトが返る。
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<Employee> findByParam(@QueryParam("postId") int postId,
			@QueryParam("empId") String empId,
			@QueryParam("nameParam") String nameParam) {
		Param param = new Param(postId, empId, nameParam);
		return empDao.findByParam(param);
	}

	/**
	 * 指定した従業員情報を登録する。
	 * EmployeeマスタとAccountマスタに登録する
	 * @param form 従業員情報（画像含む）を収めたオブジェクト。アカウント情報も含む。
	 * @return DB上のIDが振られた従業員情報
	 * @throws WebApplicationException 入力データチェックに失敗した場合に送出される。
	 */
	@POST
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(MediaType.APPLICATION_JSON)
	public Employee create(final FormDataMultiPart form) throws WebApplicationException {
		Employee employee = new Employee();

		/**
		 * Employeeの情報をつめる
		 */
		employee.setId(0);
		employee.setEmpId(form.getField("empId").getValue());
		employee.setName(form.getField("name").getValue());
		employee.setAge(Integer.parseInt(form.getField("age").getValue()));
		String gender = form.getField("gender").getValue();
		employee.setGender(Gender.valueOf(gender));

		employee.setZip(form.getField("zip").getValue());
		employee.setPref(form.getField("pref").getValue());
		employee.setAddress(form.getField("address").getValue());

		String enterDateStr = form.getField("enterDate").getValue();
		if (enterDateStr != null && !enterDateStr.isEmpty()) {
			employee.setEnterDate(enterDateStr);
		}

		String retireDateStr = form.getField("retireDate").getValue();
		if (retireDateStr != null && !retireDateStr.isEmpty()) {
			employee.setRetireDate(retireDateStr);
		}

		if (!employee.isValidObject()) {
			throw new WebApplicationException(Response.Status.BAD_REQUEST);
		}

		// Photo関連の処理
		FormDataBodyPart photoPart = form.getField("photo");
		Photo photo = createPhoto(photoPart);
		if (photo.getId() == 0) {
			throw new WebApplicationException(Response.Status.BAD_REQUEST);
		}
		employee.setPhotoId(photo.getId());

		// Post関連の処理
		int postId = Integer.parseInt(form.getField("postId").getValue());
		Post post = postDao.findById(postId);
		if (post == null) {
			throw new WebApplicationException(Response.Status.BAD_REQUEST);
		}
		employee.setPost(post);


		/**
		 * Accountの情報をつめる
		 */
		accDao.create(form.getField("empId").getValue(), form.getField("password").getValue(), form.getField("auth").getValue());



		return empDao.create(employee);
	}

	/**
	 * 指定した情報でDBを更新する。
	 *
	 * @param form 更新情報を含めた従業員情報
	 * @throws WebApplicationException 入力データチェックに失敗した場合に送出される。
	 */
	@PUT
	@Path("{id}")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(MediaType.APPLICATION_JSON)
	public Employee update(@PathParam("id") int id,
			final FormDataMultiPart form) throws WebApplicationException {
		Employee employee = new Employee();

		employee.setId(id);
		employee.setEmpId(form.getField("empId").getValue());
		employee.setName(form.getField("name").getValue());
		employee.setAge(Integer.parseInt(form.getField("age").getValue()));
		String gender = form.getField("gender").getValue();
		employee.setGender(Gender.valueOf(gender));

		employee.setZip(form.getField("zip").getValue());
		employee.setPref(form.getField("pref").getValue());
		employee.setAddress(form.getField("address").getValue());

		String enterDateStr = form.getField("enterDate").getValue();
		if (enterDateStr != null && !enterDateStr.isEmpty()) {
			employee.setEnterDate(enterDateStr);
		}

		String retireDateStr = form.getField("retireDate").getValue();
		if (retireDateStr != null && !retireDateStr.isEmpty()) {
			employee.setRetireDate(retireDateStr);
		}

		if (!employee.isValidObject()) {
			throw new WebApplicationException(Response.Status.BAD_REQUEST);
		}

		// Photo関連の処理
		String photoIdSrc = form.getField("photoId").getValue();
		int photoId = Integer.parseInt(photoIdSrc);
		FormDataBodyPart photoPart = form.getField("photo");
		if (!photoPart.getContentDisposition().getFileName().isEmpty()) {
			updatePhoto(photoId, photoPart);
		}
		employee.setPhotoId(photoId);

		// Post関連の処理
		int postId = Integer.parseInt(form.getField("postId").getValue());
		Post post = postDao.findById(postId);
		if (post == null) {
			throw new WebApplicationException(Response.Status.BAD_REQUEST);
		}
		employee.setPost(post);

		return empDao.update(employee);
	}

	/**
	 * 指定したIDの社員情報を削除する。同時に画像データも削除する。同時にアカウント情報も削除する。
	 *
	 * @param id 削除対象の社員情報のID
	 */
	@DELETE
	@Path("{id}")
	public void remove(@PathParam("id") int id) {
		Employee employee = empDao.findById(id);
		empDao.remove(id);
		photoDao.remove(employee.getPhotoId());
		accDao.remove(id);
	}

	@GET
	@Path("csv")
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	public Response downloadCsv() {
		Param param = new Param(0, "", "");
		List<Employee> list = empDao.findByParam(param);

		String header = "ID,社員番号,名前,年齢,性別,写真ID,郵便番号,都道府県,住所,所属部署ID,入社日付,退社日付"
				+ System.getProperty("line.separator");
		StringBuffer csvContents = new StringBuffer(header);

		for (Employee employee : list) {
			String line = employee.toString() + System.getProperty("line.separator");
			csvContents.append(line);
		}

		return Response.status(Status.OK)
				.entity(csvContents.toString())
				.header("Content-disposition", "attachment; filename=employee.csv")
				.build();
	}

	/**
	 * Formから渡されたデータを使用してPhotoデータを登録する。
	 *
	 * @param photoPart Formから渡されたPhotoデータ
	 * @return 登録されてIDが振られたPhotoインスタンス
	 */
	private Photo createPhoto(FormDataBodyPart photoPart) {
		Photo photo = build(photoPart);

		return photoDao.create(photo);
	}

	/**
	 * Formから渡されたデータを使用してPhotoデータを更新する。
	 *
	 * @param photoId 更新対象のPhotoのID
	 * @param photoPart Formから渡されたPhotoデータ
	 * @return 正常に更新された場合はtrue、失敗した場合はfalse
	 */
	private boolean updatePhoto(int photoId, FormDataBodyPart photoPart) {
		Photo photo = build(photoPart);
		photo.setId(photoId);
		return photoDao.update(photo);
	}

	/**
	 * formから渡されたデータを使用してPhotoインスタンスを構築する。
	 *
	 * @param photoPart Formから渡されたPhotoデータ
	 * @return ID以外のフィールドに値がセットされたPhotoインスタンス
	 */
	private Photo build(FormDataBodyPart photoPart) {
		Photo photo = new Photo();
		ContentDisposition photoInfo = photoPart.getContentDisposition();

		photo.setFileName(photoInfo.getFileName());

		photo.setContentType(photoPart.getMediaType().toString());

		BodyPartEntity bodyPartEntity = (BodyPartEntity)photoPart.getEntity();
		InputStream in = bodyPartEntity.getInputStream();
		photo.setPhoto(in);

		photo.setEntryDate(new Date(System.currentTimeMillis()));
		return photo;
	}
}
