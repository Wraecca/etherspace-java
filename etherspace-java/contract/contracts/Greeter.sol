pragma solidity >=0.4.22 <0.6.0;

// Modified Greeter contract. Based on example at https://www.ethereum.org/greeter.

contract mortal {
    /* Define variable owner of the type address */
    address owner;

    /* This constructor is executed at initialization and sets the owner of the contract */
    constructor() public {owner = msg.sender;}

    /* Function to recover the funds on the contract */
    function kill() public {if (msg.sender == owner) selfdestruct(msg.sender);}
}

contract greeter is mortal {
    /* define variable greeting of the type string */
    string greeting;

    /* this runs when the contract is executed */
    constructor(string memory _greeting) public {
        greeting = _greeting;
    }

    /* main function */
    function greet() public view returns (string memory) {
        return greeting;
    }

    function newGreeting(string memory _greeting) public {
        emit Modified(greeting, _greeting, greeting, _greeting);
        greeting = _greeting;
    }

    /* we include indexed events to demonstrate the difference that can be
    captured versus non-indexed */
    event Modified(
        string indexed oldGreetingIdx, string indexed newGreetingIdx,
        string oldGreeting, string newGreeting);

    function twoDimensionArray(uint[5][] memory array, uint row, uint col) public view returns (uint) {
        return array[row][col];
    }

    function boolType(bool a) public view returns (bool) {
        return a;
    }

    function intType(int a) public view returns (int) {
        return a;
    }

    function uintType(uint a) public view returns (uint) {
        return a;
    }

    function int24Type(int24 a) public view returns (int24) {
        return a;
    }
}
