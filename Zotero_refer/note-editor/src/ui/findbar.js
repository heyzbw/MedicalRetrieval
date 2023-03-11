'use strict';

import React, { useState, useEffect, useRef, useCallback } from 'react';
import { FormattedMessage, useIntl } from 'react-intl';
import { IconChevronDown, IconChevronUp } from './icons';

function Findbar({ searchState, active }) {
	const intl = useIntl();
	const [showReplace, setShowReplace] = useState(false);
	const [findValue, setFindValue] = useState('');
	const [replaceValue, setReplaceValue] = useState('');
	const searchInputRef = useRef();
	const replaceInputRef = useRef();

	const handleKeydownCallback = useCallback(handleKeydown, []);

	useEffect(() => {
		window.addEventListener('keydown', handleKeydownCallback);
		return () => {
			window.removeEventListener('keydown', handleKeydownCallback);
		};
	}, [handleKeydownCallback]);

	function handleKeydown(event) {
		if ((event.ctrlKey || event.metaKey) && event.key === 'f') {
			searchState.setActive(true);
			searchInputRef.current.focus();
			searchInputRef.current.select();
			event.preventDefault();
		}
	}

	useEffect(() => {
		if (active) {
			setTimeout(() => {
				if (searchInputRef.current) {
					searchInputRef.current.focus();
					searchInputRef.current.select();
				}
			}, 100);
		}
	}, [active]);

	function handleMouseDown(event) {
		if (event.target.nodeName !== 'INPUT') {
			event.preventDefault();
		}
	}

	function handleFindPrev() {
		searchState.prev();
	}

	function handleFindNext(event) {
		searchState.next();
	}

	function handleReplace() {
		searchState.replace(replaceValue);
	}

	function handleReplaceAll() {
		searchState.replaceAll(replaceValue);
	}

	function handleFindInputChange(event) {
		setFindValue(event.target.value);
		searchState.setSearchTerm(event.target.value);
	}

	function handleReplaceInputChange(event) {
		setReplaceValue(event.target.value);
	}

	function handleFindInputKeyDown(event) {
		if (event.key === 'Escape') {
			searchState.setActive(false);
		}
		else if (event.key === 'Enter' && event.shiftKey) {
			handleFindPrev();
			event.preventDefault();
		}
		else if (event.key === 'Enter' && !event.shiftKey) {
			handleFindNext();
			event.preventDefault();
		}
	}

	function handleReplaceInputKeyDown(event) {
		if (event.key === 'Escape') {
			searchState.setActive(false);
		}
		else if (event.key === 'Enter') {
			handleReplace();
		}
		else if (event.key === 'ArrowUp') {
			handleFindPrev();
			event.preventDefault();
		}
		else if (event.key === 'ArrowDown') {
			handleFindNext();
			event.preventDefault();
		}
	}

	function handleReplaceCheckboxChange() {
		if (!showReplace) {
			setTimeout(() => {
				replaceInputRef.current.focus();
			});
		}
		setShowReplace(!showReplace);
	}

	return active && (
		<div className="findbar" onMouseDown={handleMouseDown}>
			<div className="line">
				<div className="input-box">
					<div className="input">
						<input
							ref={searchInputRef}
							type="text"
							placeholder={intl.formatMessage({ id: 'noteEditor.find' })}
							value={searchState.searchTerm || ''}
							onChange={handleFindInputChange} onKeyDown={handleFindInputKeyDown}
						/>
					</div>
					<div className="buttons">
						<button className="button" onClick={handleFindPrev} title={intl.formatMessage({ id: 'noteEditor.previous' })}>
							<IconChevronUp/>
						</button>
						<button className="button" onClick={handleFindNext} title={intl.formatMessage({ id: 'noteEditor.next' })}>
							<IconChevronDown/>
						</button>
					</div>
				</div>
				<div className="check-button">
					<input type="checkbox" id="replace-checkbox" checked={showReplace} onChange={handleReplaceCheckboxChange}/>
					<label htmlFor="replace-checkbox"><FormattedMessage id="noteEditor.replace"/></label>
				</div>
			</div>
			{showReplace && <div className="line">
				<div className="input-box">
					<div className="input">
						<input
							ref={replaceInputRef}
							type="text"
							placeholder={intl.formatMessage({ id: 'noteEditor.replace' })}
							value={replaceValue}
							onChange={handleReplaceInputChange}
							onKeyDown={handleReplaceInputKeyDown}
						/>
					</div>
					<div className="buttons">
						<button className="button text-button" onClick={handleReplace}>
							<FormattedMessage id="noteEditor.replaceNext"/>
						</button>
						<button className="button text-button" onClick={handleReplaceAll}>
							<FormattedMessage id="noteEditor.replaceAll"/>
						</button>
					</div>
				</div>
			</div>}
		</div>
	);
}

export default Findbar;
