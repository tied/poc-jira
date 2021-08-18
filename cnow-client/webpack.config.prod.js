require('webpack');
var path = require('path');
module.exports = {
    mode: 'development',
    entry: {
        crm: ['./src/App.js']
    },
    output: {
        path: path.join(__dirname, '../POCService/src/main/resources/client'),
        filename: '[name].pack.js'
    },
    module: {
        rules: [
            {
                test: /\.(js|jsx)$/,
                loader: 'babel-loader',
                query: {
                    cacheDirectory: true
                },
                exclude: /node_modules/
            },
            {
                test: /\.css$/,
                use: [ 'style-loader', 'css-loader' ]
            }
        ]
    },
    plugins: [],
    resolve: {
        extensions: [".jsx", ".js"]
    },
    externals: {
        i18nStrings: 'require("jira/nps/i18n")'
    }
};
