$(document).ready(function() {
	$('.atc-category').each(function(){
		var that = this;
		$(this).toggle(function(){
			$(that).parent().children().hide();
			$(that).show();
			$(that).addClass('done');
		}, function(){
			$(that).parent().children().show();
			$(that).removeClass('done');
		});

	});
});