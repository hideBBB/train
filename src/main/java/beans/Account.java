package beans;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Account {

	private int id;
	private String auth;

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getAuth() {
		return auth;
	}
	public void setAuth(String auth) {
		this.auth = auth;
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
        sb.append(this.getId());
        sb.append(",");
        sb.append(this.getAuth());
        // バッファに書き出します
		return sb.toString();
	}




}
