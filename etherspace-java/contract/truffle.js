const HDWalletProvider = require("truffle-hdwallet-provider");
const mnemonic = process.env.MNEMONIC;

module.exports = {
    networks: {
        development: {
            host: "127.0.0.1",
            port: 8545,
            network_id: "*"
        },
        kovan: {
            provider: function() {
                return new HDWalletProvider(mnemonic, "https://kovan.infura.io/")
            },
            network_id: 42
        },
        mainnet: {
            provider: function() {
                return new HDWalletProvider(mnemonic, "https://mainnet.infura.io/")
            },
            network_id: 1
        }
    },
    solc: {
        optimizer: {
            enabled: true,
            runs: 200
        }
    }
};