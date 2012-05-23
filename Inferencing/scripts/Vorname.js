{
	name: 'Vorname',
	script: function ($val) {
		var x = $val.substring(1,$val.length-1).split(' ');
		return '"'+x[0]+'"';
	}
};