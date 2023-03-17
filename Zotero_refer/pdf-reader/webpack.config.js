const path = require('path');
const CopyWebpackPlugin = require('copy-webpack-plugin');
const WriteFilePlugin = require('write-file-webpack-plugin');

const configDev = {
	mode: process.env.NODE_ENV === 'production' ? 'production' : 'development',
	devtool: 'source-map',
	entry: [
		'./src/index.dev.js',
		'./src/stylesheets/main.scss'
	],
	output: {
		path: path.join(__dirname, './build'),
		filename: 'dev/annotator.js',
		library: 'pdf-reader',
		libraryTarget: 'umd',
		publicPath: '/',
		umdNamedDefine: true
	},
	module: {
		rules: [
			{
				test: /\.(js|jsx)$/,
				exclude: /node_modules/,
				use: {
					loader: 'babel-loader',
					options: {
						presets: [
							'@babel/preset-react',
							[
								'@babel/preset-env',
								{
									modules: false
								}
							]
						],
						'plugins': [
							'@babel/plugin-transform-runtime',
							'@babel/plugin-proposal-class-properties'
						]
					}
				}
			},
			{
				test: /\.scss$/,
				use: [
					{
						loader: 'file-loader',
						options: {
							name: 'dev/viewer.css'
						}
					},
					{
						loader: 'extract-loader'
					},
					{
						loader: 'css-loader',
						options: {
							sourceMap: true,
							url: false
						}
					},
					{
						loader: 'postcss-loader',
						options: {
							sourceMap: true
						}
					},
					{
						loader: 'sass-loader',
						options: {
							sourceMap: true
						}
					}
				]
			}
		]
	},
	resolve: {
		extensions: ['*', '.js']
	},
	plugins: [
		new CopyWebpackPlugin([
				{ from: 'res/', to: 'dev/' }
			], { copyUnmodified: true }
		),
		new WriteFilePlugin()
	],
	devServer: {
		port: 3000,
		contentBase: path.join(__dirname, 'build/'),
		openPage: 'dev/viewer.html',
		open: false,
		// watchOptions: {
		// 	poll: true
		// }
	}
};


const configWeb = {
	mode: process.env.NODE_ENV === 'production' ? 'production' : 'development',
	entry: [
		'./src/index.web.js',
		'./src/stylesheets/main.scss'
	],
	output: {
		path: path.join(__dirname, './build/web'),
		filename: 'annotator.js',
		library: 'pdf-reader',
		libraryTarget: 'umd',
		publicPath: '/',
		umdNamedDefine: true
	},
	optimization: {
		minimize: false
	},
	module: {
		rules: [
			{
				test: /\.(js|jsx)$/,
				exclude: /node_modules/,
				use: {
					loader: 'babel-loader',
					options: {
						presets: [
							'@babel/preset-react',
							[
								'@babel/preset-env',
								{
									useBuiltIns: false,
									modules: false
								}
							]
						],
						'plugins': [
							'@babel/plugin-transform-runtime',
							'@babel/plugin-proposal-class-properties'
						]
					}
				}
			},
			{
				test: /\.scss$/,
				use: [
					{
						loader: 'file-loader',
						options: {
							name: 'viewer.css'
						}
					},
					{
						loader: 'extract-loader'
					},
					{
						loader: 'css-loader?-url'
					},
					{
						loader: 'postcss-loader'
					},
					{
						loader: 'sass-loader'
					}
				]
			}
		]
	},
	resolve: {
		extensions: ['*', '.js']
	},
	plugins: [
		new CopyWebpackPlugin([
				{ from: 'res/', to: '' }
			], { copyUnmodified: true }
		)
	],
};

const configZotero = {
	mode: process.env.NODE_ENV === 'production' ? 'production' : 'development',
	entry: [
		'./src/index.zotero.js',
		'./src/stylesheets/main.scss'
	],
	output: {
		path: path.join(__dirname, './build/zotero'),
		filename: 'annotator.js',
		library: 'pdf-reader',
		libraryTarget: 'umd',
		publicPath: '/',
		umdNamedDefine: true
	},
	optimization: {
		minimize: false
	},
	module: {
		rules: [
			{
				test: /\.(js|jsx)$/,
				exclude: /node_modules/,
				use: {
					loader: 'babel-loader',
					options: {
						presets: [
							'@babel/preset-react',
							[
								'@babel/preset-env',
								{
									useBuiltIns: false,
									modules: false
								}
							]
						],
						'plugins': [
							'@babel/plugin-transform-runtime',
							'@babel/plugin-proposal-class-properties'
						]
					}
				}
			},
			{
				test: /\.scss$/,
				use: [
					{
						loader: 'file-loader',
						options: {
							name: 'viewer.css'
						}
					},
					{
						loader: 'extract-loader'
					},
					{
						loader: 'css-loader?-url'
					},
					{
						loader: 'postcss-loader'
					},
					{
						loader: 'sass-loader'
					}
				]
			}
		]
	},
	resolve: {
		extensions: ['*', '.js']
	},
	plugins: [
		new CopyWebpackPlugin([
				{ from: 'res/', to: '' }
			], { copyUnmodified: true }
		)
	],
	externals: {
		'react': 'React',
		'react-dom': 'ReactDOM',
		'react-intl': 'ReactIntl',
		'prop-types': 'PropTypes'
	}
};

module.exports = [configDev, configWeb, configZotero];
