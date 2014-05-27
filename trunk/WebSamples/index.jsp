<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Insert title here</title>
</head>
<body>
<form action="./result.jsp" method="POST">
<input type="hidden" name="PROCESS_NAME" value="/home/kenji/workspace/sa4cob2db/tests/cobs/jni/JNIDYNTEST" />
<input type="hidden" name="INPUT_LAYOUT" value="SCR_RECORD" />
<input type="hidden" name="OUTPUT_LAYOUT" value="SCR_RECORD" />
<table>
<tr><td>PROC</td><td><select name="SCR_PROC">
<option value="0">END</option>
<option value="1">WRITE</option>
<option value="2" selected>READ</option>
<option value="3">REWRITE</option>
<option value="9">DELETE</option>
</select></td></tr>
<tr><td>ID</td><td><input type="text" name="SCR_ID" value="${SCR_ID}" /></td></tr>
<tr><td>CD</td><td><input type="text" name="SCR_CD" value="${SCR_CD}"/></td></tr>
<tr><td>NIHONGO</td><td><input type="text" name="SCR_NIHONGO" value="${SCR_NIHONGO}"/></td></tr>
<tr><td>SEISU</td><td><input type="text" name="SCR_SEISU" value="${SCR_SEISU}"/></td></tr>
<tr><td>SEISU_FLG</td><td><input type="text" name="SCR_SEISU_FLG" value="${SCR_SEISU_FLG}"/></td></tr>
<tr><td>HIZUKE_YYYY</td><td><input type="text" name="SCR_HIZUKE_YYYY" value="${SCR_HIZUKE_YYYY}"/></td></tr>
<tr><td>HIZUKE_MM</td><td><input type="text" name="SCR_HIZUKE_MM" value="${SCR_HIZUKE_MM}"/></td></tr>
<tr><td>HIZUKE_DD</td><td><input type="text" name="SCR_HIZUKE_DD" value="${SCR_HIZUKE_DD}"/></td></tr>
<tr><td>JIKOKU_HH</td><td><input type="text" name="SCR_JIKOKU_HH" value="${SCR_JIKOKU_HH}"/></td></tr>
<tr><td>JIKOKU_MM</td><td><input type="text" name="SCR_JIKOKU_MM" value="${SCR_JIKOKU_MM}"/></td></tr>
<tr><td>JIKOKU_SS</td><td><input type="text" name="SCR_JIKOKU_SS" value="${SCR_JIKOKU_SS}"/></td></tr>
<tr><td>FUDOU_1</td><td><input type="text" name="SCR_FUDOU1" value="${SCR_FUDOU1}"/></td></tr>
<tr><td>FUDOU_2</td><td><input type="text" name="SCR_FUDOU2" value="${SCR_FUDOU2}"/></td></tr>
<tr><td>FUDOU_FLG</td><td><input type="text" name="SCR_FUDOU_FLG" value="${SCR_FUDOU_FLG}"/></td></tr>
<tr><td><input type="submit"/></td><td><input type="reset"/></td></tr>
</table>
</form>
</body>
</html>
