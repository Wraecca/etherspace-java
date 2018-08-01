var Greeter = artifacts.require("../contracts/Greeter.sol");

module.exports = function (deployer) {
    deployer.deploy(Greeter, "Hello World");
};
