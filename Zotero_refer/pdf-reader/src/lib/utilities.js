'use strict';

export function isMac() {
	return !!navigator && /Mac/.test(navigator.platform);
}

export function isLinux() {
	return !!navigator && /Linux/.test(navigator.platform);
}

export function isWin() {
	return !!navigator && /Win/.test(navigator.platform);
}

export function copyToClipboard(str) {
	let el = document.createElement('textarea');
	el.value = str;
	document.body.appendChild(el);
	el.select();
	document.execCommand('copy');
	document.body.removeChild(el);
}

export function deselect() {
	let selection = window.getSelection ? window.getSelection() : document.selection ? document.selection : null;
	if (selection) selection.empty ? selection.empty() : selection.removeAllRanges();
}

export function getClientRects(range, containerEl) {
	let clientRects = Array.from(range.getClientRects());
	let offset = containerEl.getBoundingClientRect();
	let rects = clientRects.map((rect) => {
		return {
			top: rect.top + containerEl.scrollTop - offset.top - 10,
			left: rect.left + containerEl.scrollLeft - offset.left - 9,
			width: rect.width,
			height: rect.height
		};
	});

	rects = rects.map((rect) => {
		return [
			rect.left,
			rect.top,
			rect.left + rect.width,
			rect.top + rect.height
		];
	});

	return rects;
}


// https://github.com/jashkenas/underscore/blob/master/underscore.js
// (c) 2009-2018 Jeremy Ashkenas, DocumentCloud and Investigative Reporters & Editors
// Underscore may be freely distributed under the MIT license.
// Returns a function, that, when invoked, will only be triggered at most once
// during a given window of time. Normally, the throttled function will run
// as much as it can, without ever going more than once per `wait` duration;
// but if you'd like to disable the execution on the leading edge, pass
// `{leading: false}`. To disable execution on the trailing edge, ditto.
export function throttle(func, wait, options) {
	var context, args, result;
	var timeout = null;
	var previous = 0;
	if (!options) options = {};
	var later = function () {
		previous = options.leading === false ? 0 : Date.now();
		timeout = null;
		result = func.apply(context, args);
		if (!timeout) context = args = null;
	};
	return function () {
		var now = Date.now();
		if (!previous && options.leading === false) previous = now;
		var remaining = wait - (now - previous);
		context = this;
		args = arguments;
		if (remaining <= 0 || remaining > wait) {
			if (timeout) {
				clearTimeout(timeout);
				timeout = null;
			}
			previous = now;
			result = func.apply(context, args);
			if (!timeout) context = args = null;
		}
		else if (!timeout && options.trailing !== false) {
			timeout = setTimeout(later, remaining);
		}
		return result;
	};
}

export function setCaretToEnd(target) {
	const range = document.createRange();
	const sel = window.getSelection();
	range.selectNodeContents(target);
	range.collapse(false);
	sel.removeAllRanges();
	sel.addRange(range);
	target.focus();
	range.detach();
}

export function clearSelection() {
	let selection = window.getSelection ? window.getSelection() : document.selection ? document.selection : null;
	if (selection) selection.empty ? selection.empty() : selection.removeAllRanges();
}

export function getPageFromElement(target) {
	let node = target.closest('#viewer > .page') || target.closest('#viewer > .spread > .page');
	if (!node) {
		return null;
	}

	let number = parseInt(node.dataset.pageNumber);
	return { node, number };
}

export function fitRectIntoRect(rect, containingRect) {
	return [
		Math.max(rect[0], containingRect[0]),
		Math.max(rect[1], containingRect[1]),
		Math.min(rect[2], containingRect[2]),
		Math.min(rect[3], containingRect[3])
	];
}

export function findOrCreateContainerLayer(container, className) {
	let layer = container.querySelector('.' + className);

	if (!layer) {
		layer = document.createElement('div');
		layer.className = className;
		container.appendChild(layer);
	}

	return layer;
}

export function pointerEventToPosition(event) {
	let page = getPageFromElement(event.target);
	if (!page) {
		return null;
	}

	let rect = page.node.getBoundingClientRect();

	let x = event.clientX + page.node.scrollLeft - rect.left - 9;
	let y = event.clientY + page.node.scrollTop - rect.top - 10;

	return {
		pageIndex: page.number - 1,
		rects: [[x, y, x, y]]
	};
}

export function getPositionBoundingRect(position) {
	if (position.rects) {
		return [
			Math.min(...position.rects.map(x => x[0])),
			Math.min(...position.rects.map(x => x[1])),
			Math.max(...position.rects.map(x => x[2])),
			Math.max(...position.rects.map(x => x[3]))
		];
	}
	else if (position.paths) {
		let x = position.paths[0][0];
		let y = position.paths[0][1];
		let rect = [x, y, x, y];
		for (let path of position.paths) {
			for (let i = 0; i < path.length - 1; i += 2) {
				let x = path[i];
				let y = path[i + 1];
				rect[0] = Math.min(rect[0], x);
				rect[1] = Math.min(rect[1], y);
				rect[2] = Math.max(rect[2], x);
				rect[3] = Math.max(rect[3], y);
			}
		}
		return rect;
	}
}

export function positionsEqual(p1, p2) {
	if (Array.isArray(p1.rects) !== Array.isArray(p2.rects)
		|| Array.isArray(p1.paths) !== Array.isArray(p2.paths)) {
		return false;
	}

	if (p1.pageIndex !== p2.pageIndex) {
		return false;
	}

	if (p1.rects) {
		return JSON.stringify(p1.rects) === JSON.stringify(p2.rects);
	}
	else if (p1.paths) {
		return JSON.stringify(p1.paths) === JSON.stringify(p2.paths);
	}

	return false;
}

export function intersectAnnotationWithPoint(selectionPosition, pointPosition) {
	if (selectionPosition.pageIndex !== pointPosition.pageIndex) {
		return false;
	}

	let [x, y] = pointPosition.rects[0];

	if (selectionPosition.rects) {
		for (let i = 0; i < selectionPosition.rects.length; i++) {
			let [r1, r2] = selectionPosition.rects.slice(i, i + 2);
			if (!(x > r1[2]
				|| x < r1[0]
				|| y > r1[3]
				|| y < r1[1])) {
				return true;
			}

			if (!r2) {
				continue;
			}

			if (x > r1[0] && x > r2[0]
				&& x < r1[2] && x < r2[2]
				&& y < r1[3] && y > r2[1]
				&& r1[1] - r2[3] < Math.min(r1[3] - r1[1], r2[3] - r2[1])) {
				return true;
			}
		}
	}
	else if (selectionPosition.paths) {
		let maxDistance = Math.max(7, selectionPosition.width);
		for (let path of selectionPosition.paths) {
			for (let i = 0; i < path.length - 1; i += 2) {
				let ax = path[i];
				let ay = path[i + 1];
				if (Math.hypot(ax - x, ay - y) < maxDistance) {
					return true;
				}
			}
		}
	}
	return false;
}

import React, { useState, useEffect, useRef, useDebugValue } from 'react';

/**
 * Synchronously sets ref value and asynchronously sets state value
 * @param initialValue
 * @returns {[]}
 */
export function useRefState(initialValue) {
	const [state, setState] = useState(initialValue);
	const stateRef = useRef(state);

	function _setState(value) {
		stateRef.current = value;
		setState(value);
	}

	return [state, stateRef, _setState];
}

export function getAnnotationsFromSelectionRanges(selectionRanges) {
	let annotations = [];
	for (let selectionRange of selectionRanges) {
		let pageIndex = selectionRange.position.pageIndex;
		annotations.push({
			type: 'highlight',
			fromText: true,
			text: selectionRange.text,
			position: selectionRange.position,
			pageLabel: window.extractor.getCachedPageLabel(pageIndex) || '-'
		});
	}
	return annotations;
}

export function getImageDataURL(img) {
	var canvas = document.createElement('canvas');
	canvas.width = img.naturalWidth;
	canvas.height = img.naturalHeight;
	var ctx = canvas.getContext('2d');
	ctx.drawImage(img, 0, 0, img.naturalWidth, img.naturalHeight);
	return canvas.toDataURL('image/png');
}

export function setDataTransferAnnotations(dataTransfer, annotations) {
	let text = annotations.map((annotation) => {
		let formatted = '';

		if (annotation.text) {
			let text = annotation.text.trim();
			if (annotation.fromText) {
				formatted = text;
			}
			else {
				formatted = '“' + text + '”';
			}
		}

		let comment = annotation.comment && annotation.comment.trim();
		if (comment) {
			if (formatted) {
				formatted += comment.includes('\n') ? '\n' : ' ';
			}
			formatted += comment;
		}

		return formatted;
	}).filter(x => x).join('\n\n');

	let fromText = annotations.some(x => x.fromText);

	annotations = annotations.map(
		({ id, type, text, color, comment, image, position, pageLabel, tags }) => {
			if (image) {
				let img = document.querySelector(`[data-sidebar-annotation-id="${id}"] img`);
				if (img) {
					image = getImageDataURL(img);
				}
			}
			return {
				id,
				type,
				attachmentItemID: window.itemID,
				text: text ? text.trim() : text,
				color,
				comment: comment ? comment.trim() : comment,
				image,
				position,
				pageLabel,
				tags
			};
		}
	);

	// Clear image data set on some untested type (when drag is initiated on img),
	// which also prevents word processors from using `text/plain`, and
	// results to dumped base64 content (LibreOffice) or image (Google Docs)
	dataTransfer.clearData();
	dataTransfer.setData('zotero/annotation', JSON.stringify(annotations));

	// Don't use Note translators if copying text from a PDF page
	if (fromText) {
		dataTransfer.setData('text/plain', text);
	}
	else {
		zoteroSetDataTransferAnnotations(dataTransfer, annotations);
	}
}
