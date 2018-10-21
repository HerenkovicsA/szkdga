$(document).ready(function() {
	bindListeners();

});

function bindListeners() {
	document.getElementById("openNav").onclick = function() {
		console.log("asdsa");
	    document.getElementById("mySidenav").style.width = "250px";
	    document.getElementById("main").style.marginLeft = "250px";
	    document.body.style.backgroundColor = "rgba(0,0,0,0.4)";
	};
	
	document.getElementById("closebtn").onclick = function () {
	    document.getElementById("mySidenav").style.width = "0";
	    document.getElementById("main").style.marginLeft= "0";
	    document.body.style.backgroundColor = "white";
	};
}
