$(document).ready(function() {
	$('.toggler').each(function(){
		var that = this;
		$(this).toggle(function(){
			$(that).parent().children().hide();
			$(that).show();
			$(that).parent().find('.atc-category').show();
			$(that).addClass('done');
		}, function(){
			$(that).parent().children().show();
			$(that).removeClass('done');
		});

	});
	
	$('.frequency').each(function(){
		
		var frequency = $(this).text();
		$(this).css('font-size', frequency*10 + "px");
	});
});