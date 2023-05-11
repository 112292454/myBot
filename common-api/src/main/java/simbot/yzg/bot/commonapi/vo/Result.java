package simbot.yzg.bot.commonapi.vo;

import com.alibaba.fastjson2.annotation.JSONField;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Data;

import java.io.Serializable;
import java.util.List;


/**
 * @author gzy
 * @date 2022-11-09
 */
@Data
@JsonSerialize
@JsonDeserialize
public class Result<T> implements Serializable {
	private static final long serialVersionUID = 8798754985418965184L;

	@JSONField(name="status_code")
	private Integer statusCode;

	@JSONField(name="msg")
	private String msg;

	@JSONField(name="data",serializeUsing = List.class,deserializeUsing = List.class)
	private T data;

	public static <T> Result<T> success(String msg) {
		Result<T> result = new Result<>();
		result.statusCode = HttpServletResponse.SC_OK;
		result.msg = msg;
		return result;
	}

	public static <T> Result<T> success() {
		Result<T> result = new Result<>();
		result.statusCode = HttpServletResponse.SC_OK;
		result.msg = "请求成功";
		return result;
	}
	public static <T> Result<T> error() {
		Result<T> result = new Result<>();
		result.statusCode = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
		result.msg = "执行异常";
		return result;
	}

	public static <T> Result<T> error(String msg) {
		Result<T> result = new Result<>();
		result.statusCode = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
		result.msg ="执行异常:" + msg;
		return result;
	}

	public static <T> Result<T> error(Integer statusCode, String msg) {
		Result<T> result = new Result<>();
		result.statusCode = statusCode;
		result.msg = msg;
		return result;
	}

	public Result<T> data(T data) {
		this.data = data;
		return this;
	}

	public boolean isSuccess() {
		return statusCode == HttpServletResponse.SC_OK;
	}
}
