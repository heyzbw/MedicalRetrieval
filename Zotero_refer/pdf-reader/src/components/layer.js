'use strict';

import React, { useState, useEffect, useLayoutEffect, useRef, useCallback, useMemo } from 'react';
import ReactDOM from 'react-dom';
import cx from 'classnames';
import Highlight from './highlight';
import Note from './note';
import Area from './area';
import AreaSelector from './area-selector';
import SelectionMenu from './selection-menu';
import PagePopup from './page-popup';
import { PopupPreview } from './preview';

import { p2v as p2vc, v2p as v2pc, wx, hy } from '../lib/coordinates';

import {
	throttle,
	findOrCreateContainerLayer,
	pointerEventToPosition,
	setDataTransferAnnotations
} from '../lib/utilities';
import Ink from './ink';

function PageLayerInk(props) {
	function getContainerNode(viewport) {
		return findOrCreateContainerLayer(
			viewport.div,
			'layer-ink'
		);
	}

	let node = getContainerNode(props.view);
	if (!node) return null;

	return ReactDOM.createPortal(
		<div className={cx({ 'selecting-annotation': !!props.selectedAnnotationIDs.length })}>
			{props.annotations.map(
				(annotation, index) => {
					let { position, ...rest } = annotation;

					let viewportAnnotation = {
						position: p2vc(position, props.view.viewport),
						...rest
					};

					return (
						<div key={annotation.id}>
							<Ink
								annotation={viewportAnnotation}
								isSelected={props.selectedAnnotationIDs.includes(annotation.id)}
							/>
						</div>
					);
				}
			)}
		</div>,
		node
	);
}

function PageLayerHighlight(props) {
	function getContainerNode(viewport) {
		return findOrCreateContainerLayer(
			viewport.div,
			'layer-highlight'
		);
	}

	let node = getContainerNode(props.view);
	if (!node) return null;

	return ReactDOM.createPortal(
		<div className={cx({ 'selecting-annotation': !!props.selectedAnnotationIDs.length })}>
			{props.annotations.map(
				(annotation, index) => {
					let { position, ...rest } = annotation;

					let viewportAnnotation = {
						position: p2vc(position, props.view.viewport),
						...rest
					};

					return (
						<div key={annotation.id}>
							<Highlight
								annotation={viewportAnnotation}
								isSelected={props.selectedAnnotationIDs.includes(annotation.id)}
								onDragStart={props.onDragStart}
								onDragEnd={props.onDragEnd}
							/>
						</div>
					);
				}
			)}
		</div>,
		node
	);
}

function PageLayerNote(props) {
	function getContainerNode(viewport) {
		return findOrCreateContainerLayer(
			viewport.div,
			'layer-note'
		);
	}

	let node = getContainerNode(props.view);
	if (!node) return null;

	return ReactDOM.createPortal(
		<div>
			{props.annotations.map(
				(annotation, index) => {
					let { position, ...rest } = annotation;

					let viewportAnnotation = {
						position: p2vc(position, props.view.viewport),
						...rest
					};

					return (
						<div key={annotation.id}>
							<Note
								annotation={viewportAnnotation}
								isSelected={props.selectedAnnotationIDs.includes(annotation.id)}
								enableMoving={props.selectedAnnotationIDs.length === 1}
								onDragStart={props.onDragStart}
								onDragEnd={props.onDragEnd}
								onChangePosition={(position) => {
									props.onChangePosition(annotation.id, position);
								}}
							/>
						</div>
					);
				}
			)}
		</div>,
		node
	);
}

function PageLayerArea(props) {
	function getContainerNode(viewport) {
		return findOrCreateContainerLayer(
			viewport.div,
			'layer-area'
		);
	}

	let node = getContainerNode(props.view);
	if (!node) return null;

	return ReactDOM.createPortal(
		<div>
			{props.annotations.map(
				(annotation, index) => {
					let { position, ...rest } = annotation;

					let viewportAnnotation = {
						position: p2vc(position, props.view.viewport),
						...rest
					};

					return (
						<div key={annotation.id}>
							<Area
								annotation={viewportAnnotation}
								isSelected={props.selectedAnnotationIDs.includes(annotation.id)}
								move={props.selectedAnnotationIDs.length === 1}
								onResizeStart={props.onResizeStart}
								onDragStart={props.onDragStart}
								onDragEnd={props.onDragEnd}
								onChangePosition={(position) => {
									props.onChangePosition(annotation.id, position);
								}}
							/>
						</div>
					);
				}
			)}
		</div>,
		node
	);
}

function AreaSelectorLayer(props) {
	function getContainerNode() {
		let container = document.getElementById('areaSelectorContainer');
		if (!container) {
			let viewerContainer = document.getElementById('viewerContainer');
			container = document.createElement('div');
			container.id = 'areaSelectorContainer';
			viewerContainer.appendChild(container);
		}
		return container;
	}

	return ReactDOM.createPortal(
		<AreaSelector
			color={props.color}
			shouldStart={props.enableAreaSelector}
			onSelectionStart={props.onSelectionStart}
			onSelection={props.onSelection}
		/>,
		getContainerNode()
	);
}

function EdgeNoteLayer(props) {
	function getContainerNode(viewport) {
		return findOrCreateContainerLayer(
			viewport.div,
			'layer-edge-note'
		);
	}

	function quickIntersectRect(r1, r2) {
		return !(r2[0] > r1[2]
			|| r2[2] < r1[0]
			|| r2[1] > r1[3]
			|| r2[3] < r1[1]);
	}

	function stackNotes(notes) {
		notes.sort((a, b) => a.rect[0] - b.rect[0]);
		for (let note of notes) {
			for (let note2 of notes) {
				if (note2 === note) break;

				if (quickIntersectRect(note.rect, note2.rect)) {
					let shift = wx(note2.rect) / 3 * 2;
					note.rect[0] = note2.rect[0] + shift;
					note.rect[2] = note2.rect[2] + shift;
				}
			}
		}
	}

	function getNotes(annotations, viewport) {
		let notes = [];
		let scale = PDFViewerApplication.pdfViewer._currentScale;

		let width = 9.6 * scale;
		let height = 9.6 * scale;

		for (let annotation of annotations) {
			let viewportPosition = p2vc(annotation.position, viewport);
			let left = viewportPosition.rects[0][0] - width / 2;
			let top = viewportPosition.rects[0][1] - height + height / 3;
			notes.push({
					annotation,
					rect: [
						left,
						top,
						left + width,
						top + height
					]
				}
			);
		}

		notes.reverse();
		return notes;
	}


	let node = getContainerNode(props.view);
	if (!node) return null;

	let commentedAnnotations = props.annotations.filter(x => x.comment);
	let notes = getNotes(commentedAnnotations, props.view.viewport);
	stackNotes(notes);

	return ReactDOM.createPortal(
		<div>
			{notes.map(
				(note) => {
					let isSelected = props.selectedAnnotationIDs.includes(note.annotation.id);
					return (
						<div
							key={note.annotation.id}
							className={cx('edge-note', { selected: isSelected })}
							style={{
								left: note.rect[0],
								top: note.rect[1],
								width: wx(note.rect),
								height: hy(note.rect),
								color: note.annotation.color,
								zIndex: isSelected ? 2 : 1
							}}
							onClick={(e) => {
								e.preventDefault();
								e.stopPropagation();
								// Call after all other events
								setTimeout(() => {
									props.onClick(note.annotation.id);
								}, 0);
							}}
						>
							<svg width={wx(note.rect)} height={hy(note.rect)} viewBox="0 0 12 12">
								<path fill="currentColor" d="M0,0V6.707L5.293,12H12V0Z"/>
								<path d="M0,0V6.707L5.293,12H12V0ZM1.707,7H5v3.293ZM11,11H6V6H1V1H11Z" opacity="0.67"/>
								<polygon points="1.707 7 5 10.293 5 7 1.707 7" fill="#fff" opacity="0.4"/>
							</svg>
						</div>
					);
				}
			)}
		</div>,
		node
	);
}

function BlinkLayer(props) {
	const intervalRef = useRef(null);
	const innerRef = useRef(null);

	useLayoutEffect(() => {
		fade(innerRef.current);
	}, [props.id]);

	function fade(element) {
		if (intervalRef.current) clearInterval(intervalRef.current);
		let op = 1;
		intervalRef.current = setInterval(() => {
			if (op <= 0.05) {
				clearInterval(intervalRef.current);
				element.style.opacity = 0;
				return;
			}
			element.style.opacity = op;
			op -= op * 0.1;
		}, 100);
	}

	function getContainerNode(viewport) {
		return findOrCreateContainerLayer(viewport.div, 'layer-blink');
	}

	let node = getContainerNode(props.view);
	if (!node) return null;

	return ReactDOM.createPortal(
		<div ref={innerRef}>
			{props.position.rects.map((rect, index) => (
				<div
					key={index}
					className="rect"
					style={{
						left: rect[0],
						top: rect[1],
						width: rect[2] - rect[0],
						height: rect[3] - rect[1]
					}}
				/>
			))}
		</div>,
		node
	);
}

function SelectionLayer({ positions, color }) {
	useEffect(() => {
		draw();
		return () => {
			erase();
		};
	}, [positions]);

	function getCtx(pageIndex) {
		let viewport = window.PDFViewerApplication.pdfViewer.getPageView(pageIndex);
		let canvas = viewport.div.querySelector('.canvasWrapper .selectionCanvas');
		if (!canvas) {
			let wrapper = viewport.div.querySelector('.canvasWrapper');
			if (!wrapper) return null;
			canvas = document.createElement('canvas');
			canvas.className = 'selectionCanvas';
			canvas.width = viewport.canvas.width;
			canvas.height = viewport.canvas.height;
			canvas.style.width = viewport.div.style.width;
			canvas.style.height = viewport.div.style.height;
			wrapper.appendChild(canvas);
		}

		let ctx = canvas.getContext('2d');
		ctx.clearRect(0, 0, canvas.width, canvas.height);

		return ctx;
	}

	function erase() {
		for (let i = 0; i < window.PDFViewerApplication.pdfDocument.numPages; i++) {
			let page = window.PDFViewerApplication.pdfViewer.getPageView(i);
			let canvas = page.div.querySelector('.canvasWrapper .selectionCanvas');
			if (canvas) {
				let ctx = canvas.getContext('2d');
				ctx.clearRect(0, 0, canvas.width, canvas.height);
			}
		}
	}

	function draw() {
		for (let position of positions) {
			let ctx = getCtx(position.pageIndex);
			if (!ctx) return null;
			let viewport = window.PDFViewerApplication.pdfViewer.getPageView(position.pageIndex);
			ctx.fillStyle = color;
			for (let rect of position.rects) {
				let { sx, sy } = viewport.outputScale;
				ctx.fillRect(rect[0] * sx, rect[1] * sy, (rect[2] - rect[0]) * sx, (rect[3] - rect[1]) * sy);
			}
		}
	}

	return null;
}

function Layer(props) {
	const [initialized, setInitialized] = useState(false);
	const [render, setRender] = useState({});

	let viewer = window.PDFViewerApplication.pdfViewer;
	let containerNode = document.getElementById('viewerContainer');

	let selectionPositions = useMemo(() => props.selectionPositions.map(p => p2v(p)), [props.selectionPositions]);

	const handlePointerDownCallback = useCallback(handlePointerDown, []);
	const handlePointerMoveCallback = useMemo(() => throttle(handlePointerMove, 50), []);
	const handlePointerUpDownCallback = useCallback(handlePointerUp, []);
	const handlePageRenderCallback = useCallback(handlePageRender, []);

	useEffect(() => {
		containerNode = document.getElementById('viewerContainer');
		// event.detail doesn't work with pointerdown
		containerNode.addEventListener('mousedown', handlePointerDownCallback);
		containerNode.addEventListener('mousemove', handlePointerMoveCallback);
		containerNode.addEventListener('pointerup', handlePointerUpDownCallback);

		viewer = window.PDFViewerApplication.pdfViewer;
		viewer.eventBus.on('pagerendered', handlePageRenderCallback);

		return () => {
			containerNode.removeEventListener('mousedown', handlePointerDownCallback);
			containerNode.removeEventListener('mousemove', handlePointerMoveCallback);
			containerNode.removeEventListener('pointerup', handlePointerUpDownCallback);
			viewer.eventBus.off('pagerendered', handlePageRenderCallback);
		};
	}, []);

	function handlePointerDown(event) {
		let position = pointerEventToPosition(event);
		if (!position) {
			return;
		}
		props.onPointerDown(v2p(position), event);
	}

	function handlePointerMove(event) {
		let position = pointerEventToPosition(event);
		if (!position) {
			return;
		}
		if (viewer.pdfDocument.uninitialized) {
			return;
		}
		props.onPointerMove(v2p(position), event);
	}

	function handlePointerUp(event) {
		if (event.target.classList.contains('edge-note')) {
			return;
		}

		let position = pointerEventToPosition(event);
		if (!position) {
			return;
		}
		// Shoot the event after all other events are emitted.
		// Otherwise the resize updating in the area annotation is emitted too late
		// setTimeout(() => {
		props.onPointerUp(v2p(position), event);
		// }, 0);
	}

	function handlePageRender(event) {
		// console.log(`pdf.js rendered pageIndex ${event.pageNumber - 1}`);
		setRender({});
		setInitialized(true);
	}

	function groupAnnotationsByPage(annotations) {
		return [...annotations]
		.reduce((res, annotation) => {
			let { pageIndex } = annotation.position;
			res[pageIndex] = res[pageIndex] || [];
			res[pageIndex].push(annotation);
			return res;
		}, {});
	}

	function v2p(position) {
		let viewport = viewer.getPageView(position.pageIndex).viewport;
		return v2pc(position, viewport);
	}

	function p2v(position) {
		let viewport = viewer.getPageView(position.pageIndex).viewport;
		return p2vc(position, viewport);
	}

	let {
		annotations,
		color,
		selectedAnnotationIDs,
		popupAnnotation,
		blink,
		onChange,
		onClickTags,
		onClickEdgeNote
	} = props;

	if (!initialized) return null;

	let pageLayers = [];
	let annotationsByPage = groupAnnotationsByPage(annotations);
	for (let pageIndex = 0; pageIndex < viewer.pdfDocument.numPages; pageIndex++) {
		if (!annotationsByPage[pageIndex] || viewer._pages[pageIndex].renderingState === 0) {
			continue;
		}
		let view = viewer.getPageView(pageIndex);

		pageLayers.push(
			<PageLayerInk
				key={'i_' + pageIndex}
				view={view}
				selectedAnnotationIDs={selectedAnnotationIDs}
				annotations={(annotationsByPage[pageIndex].filter(x => x.type === 'ink') || [])}
			/>,
			<PageLayerHighlight
				key={'h_' + pageIndex}
				view={view}
				selectedAnnotationIDs={selectedAnnotationIDs}
				annotations={(annotationsByPage[pageIndex].filter(x => x.type === 'highlight') || [])}
				onDragStart={props.onDragStart}
				onDragEnd={props.onDragEnd}
			/>,
			<PageLayerNote
				key={'n_' + pageIndex}
				view={view}
				selectedAnnotationIDs={selectedAnnotationIDs}
				annotations={(annotationsByPage[pageIndex].filter(x => x.type === 'note') || [])}
				onChangePosition={(id, position) => {
					onChange({ id, position: v2p(position) });
				}}
				onDragStart={props.onDragStart}
				onDragEnd={props.onDragEnd}
			/>,
			<PageLayerArea
				key={'a_' + pageIndex}
				view={view}
				selectedAnnotationIDs={selectedAnnotationIDs}
				annotations={(annotationsByPage[pageIndex].filter(x => x.type === 'image') || [])}
				onResizeStart={props.onAreaResizeStart}
				onChangePosition={(id, position) => {
					onChange({ id, position: v2p(position) });
				}}
				onDragStart={props.onDragStart}
				onDragEnd={props.onDragEnd}
			/>
		);

		if (props.enableEdgeNotes) {
			pageLayers.push(<EdgeNoteLayer
				key={'m_' + pageIndex}
				view={view}
				selectedAnnotationIDs={selectedAnnotationIDs}
				annotations={(annotationsByPage[pageIndex].filter(x => ['highlight', 'image'].includes(x.type)) || [])}
				onClick={onClickEdgeNote}
			/>);
		}
	}

	if (popupAnnotation) {
		let { position, ...rest } = popupAnnotation;
		popupAnnotation = {
			position: p2v(position),
			...rest
		};
	}


	let blinkLayer = null;
	if (blink) {
		let view = viewer.getPageView(blink.position.pageIndex);
		if (view && view.renderingState !== 0) {
			let id = blink.id;
			let position = p2v(blink.position);
			blinkLayer = <BlinkLayer view={view} id={id} position={position}/>;
		}
	}

	return (
		<React.Fragment>
			{blinkLayer}
			<SelectionLayer positions={selectionPositions} color={props.selectionColor}/>
			<AreaSelectorLayer
				color={color}
				enableAreaSelector={props.enableAreaSelector}
				onSelectionStart={props.onAreaSelectionStart}
				onSelection={(position) => {
					props.onAreaSelection(v2p(position));
				}}
			/>
			{
				popupAnnotation && (
					<PagePopup
						id={popupAnnotation.id}
						className="annotation-popup"
						position={popupAnnotation.position}
						// TODO: After area resize popup still needs to be repositioned
						updateOnPositionChange={false}
					>
						<PopupPreview
							annotation={popupAnnotation}
							isExpandable={false}
							enableText={false}
							enableImage={false}
							enableComment={!popupAnnotation.readOnly || popupAnnotation.comment}
							enableTags={!popupAnnotation.readOnly || popupAnnotation.tags.length > 0}
							onUpdate={(comment) => {
								onChange({ id: popupAnnotation.id, comment });
							}}
							onColorChange={(color) => {
								onChange({ id: popupAnnotation.id, color });
							}}
							onDoubleClickPageLabel={props.onDoubleClickPageLabel}
							onClickTags={onClickTags}
							onChange={onChange}
							onPageMenu={props.onPageMenu}
							onMoreMenu={props.onMoreMenu}
							onDragStart={(event) => {
								setDataTransferAnnotations(event.dataTransfer, [popupAnnotation]);
							}}
						/>
					</PagePopup>
				)
			}
			{
				props.enableSelectionPopup && selectionPositions.length && (
					<PagePopup
						id={1}
						className="selection-popup"
						position={selectionPositions[0]}
						updateOnPositionChange={true}
					>
						<SelectionMenu
							color={color}
							enableAddToNote={props.enableAddToNote}
							onHighlight={props.onHighlightSelection}
							onCopy={props.onCopySelection}
							onAddToNote={props.onAddToNoteSelection}
						/>
					</PagePopup>
				)
			}
			{pageLayers}
		</React.Fragment>
	);
}

export default Layer;
