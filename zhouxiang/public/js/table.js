//增删表格的行数
var id = 1;

function add(text1, text2, table_id) {
    id++;
    var theTable = document.getElementById(table_id); //table的id
    var rowCount = theTable.rows.length; //获得当前表格的行数
    var row = theTable.insertRow(rowCount); //在tale里动态的增加tr
    row.id = id;
    $("#" + id).attr("onmouseover", "this.style.backgroundColor=\'#ffff66\';");
    $("#" + id).attr("onmouseout", "this.style.backgroundColor=\'#C6E2FF\';");
    var cell1 = row.insertCell(0); //在tr中动态的增加td
    var cell2 = row.insertCell(0); //在tr中动态的增加td
    cell1.innerText = text1;
    cell1.style.cssText = "text-align:center"; //tr中内容居中显示
    cell2.innerText = text2;
    cell2.style.cssText = "text-align:center"; //tr中内容居中显示
}

function moveTr(tr_id, table_id) {
    var tb = document.getElementById(table_id); //获取table
    var tr = document.getElementById(tr_id); //根据id获取具体的tr
    tb.deleteRow(tr.rowIndex); //删除行
    unit(); //调用此方法，对界面有些地方的value进行更新
}