var that = {};

var onmessage = function(fn, msg) {
	that[fn](decodeURIComponent(msg));
};

that.CN_FAVORITED = 'favorited';

that.insert = function(html) {
	html = $(html).hide();
	var sections = html.find('div.section');
	if (!sections[0]) { return; }

	sections.each(function() {
		var section = $(this);
		section.click(function() {
			android.dialogize(section.attr('title'), section.hasClass(that.CN_FAVORITED));
		});
	});

	var pos = $('div.article:first');
	if (pos[0]) {
		html.insertBefore(pos.addClass('old')).fadeIn('slow');
	} else {
		$('div.loader').hide();
		html.appendTo($('body')).fadeIn('slow');
	}
};

that.favorite = function(id) {
	$('div[title="' + id + '"]').addClass(that.CN_FAVORITED);
}

that.destroy = function(id) {
	$('div[title="' + id + '"]').removeClass(that.CN_FAVORITED);
}

that.setInterval = function(interval) {
	that.tid = setInterval((function() {
		android.update();
		return arguments.callee;
	})(), parseInt(interval, 10) * 1000);
};

that.clearInterval = function() {
	clearInterval(that.tid);
};

jQuery(function($) {
	android.getInterval();
});
