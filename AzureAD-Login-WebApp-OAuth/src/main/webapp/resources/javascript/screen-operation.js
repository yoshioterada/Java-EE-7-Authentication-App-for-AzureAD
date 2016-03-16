if (window.addEventListener) { //for W3C DOM
    window.addEventListener("load", init, false);
} else if (window.attachEvent) { //for IE
    window.attachEvent("onload", init);
} else {
    window.onload = init;
}

function init() {
    //起動時は グループ部分は非表示
    showUserTable();
}

function showUserTable() {
    document.getElementById("form:usertable").style.display = "block";
    document.getElementById("form:grouptable").style.display = "none";
}

function showGroupTable() {
    document.getElementById("form:usertable").style.display = "none";
    document.getElementById("form:grouptable").style.display = "block";
}
