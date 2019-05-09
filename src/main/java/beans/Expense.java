package beans;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Expense {
	private int id;
	private String reqDate;
	private String up_Date;
	private int reqEmpId;
	private int up_EmpId;
	private String title;
	private String payDest;
	private int amount;
	private String status;

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getReqDate() {
		return reqDate;
	}
	public void setReqDate(String reqDate) {
		this.reqDate = reqDate;
	}
	public String getUp_Date() {
		return up_Date;
	}
	public void setUp_Date(String up_Date) {
		this.up_Date = up_Date;
	}
	public int getReqEmpId() {
		return reqEmpId;
	}
	public void setReqEmpId(int reqEmpId) {
		this.reqEmpId = reqEmpId;
	}
	public int getUp_EmpId() {
		return up_EmpId;
	}
	public void setUp_EmpId(int up_EmpId) {
		this.up_EmpId = up_EmpId;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getPayDest() {
		return payDest;
	}
	public void setPayDest(String payDest) {
		this.payDest = payDest;
	}
	public int getAmount() {
		return amount;
	}
	public void setAmount(int amount) {
		this.amount = amount;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
        sb.append(this.getId());
        sb.append(",");
        sb.append(this.getReqDate());
        sb.append(",");
        sb.append(this.getUp_Date());
        sb.append(",");
        sb.append(this.getReqEmpId());
        sb.append(",");
        sb.append(this.getUp_EmpId());
        sb.append(",");
        sb.append(this.getTitle());
        sb.append(",");
        sb.append(this.getPayDest());
        sb.append(",");
        sb.append(this.getAmount());
        sb.append(",");
        sb.append(this.getStatus());
        // バッファに書き出します
		return sb.toString();
	}





}
