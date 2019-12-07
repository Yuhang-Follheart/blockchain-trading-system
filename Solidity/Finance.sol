pragma solidity >=0.4.24;
contract Finance{
    struct Debt{
        bool isValid;
        uint endTime;
        address from;
        address to;
        uint value;
    }

    struct Transaction{
        bool isValid;
        uint endTime;
        bool isUseBalance;
        bool isTransferRe;
        address sender;
		string sendername;
        address recevier;
		string receviername;
        uint value;
    }

    struct Enterprise {
        bool isValid;
        address addr;
        string name;
        bool isBank;
        int balance;
        uint receivalblesNum;
        uint receivalblesAmount;
        uint debtsNum;
        uint debtsAmount;
        uint transactionNum;
        mapping(uint => uint) transactionsIndex;
        mapping(uint => uint) debtsIndex;
        mapping(uint => uint) receivalblesIndex;
    }
    uint public debtsId;
    uint public transactionsId;
    address[] public enterpriseAddr;
    string[] public names;
    uint public enterpriseNum;
    mapping (address => Enterprise) public enterpriseList;
    mapping (uint => Debt) public debtsList;
    mapping (uint => Transaction) public transactionsList;
    mapping (bytes32 => address) public nameMapaddr;
    function isRegisted() public view returns (bool){
    	return enterpriseList[msg.sender].isValid;
    }

    function getName(address addr) public view returns (string memory){
    	string memory name = enterpriseList[addr].name;
    	return name;
    }

    function nameIsUsed(string name) public view returns(bool){
		address a = 0;
		return !(nameMapaddr[keccak256(abi.encodePacked(name))] == a);	
    }

    function name2addr(string name) public view returns(address){
		return nameMapaddr[keccak256(abi.encodePacked(name))];
    }

    function register (string memory _name, bool _isBank, int _balance) public returns (bool){
        require(!enterpriseList[msg.sender].isValid, "Your enterprise has been registered!");
		require(!nameIsUsed(_name));
        enterpriseList[msg.sender] = Enterprise(true, msg.sender, _name, _isBank, _balance, 0, 0, 0, 0, 0);
        enterpriseAddr.push(msg.sender);
		nameMapaddr[keccak256(abi.encodePacked(_name))] = msg.sender;
		names.push(_name);
		enterpriseNum++;
        return true;
    }

    function getEnterprise() public view returns(address[] memory){
        return enterpriseAddr;
    }

    function getBalance() public view returns (int){
        require(enterpriseList[msg.sender].isValid, "Your enterprise hasn't been registered yet!");
        return enterpriseList[msg.sender].balance;
    }

    function payByBalance(address _to, uint _value) public returns(bool){
        require(enterpriseList[msg.sender].isValid, "Your enterprise hasn't been registered yet!");
        require(enterpriseList[_to].isValid, "Target enterprise hasn't been registered yet!");
        require(msg.sender != _to, "You can't trade with yourself!");
        require(_value > 0, "The amount paid should be greater than zero!");
        require(enterpriseList[msg.sender].balance >= int(_value), "You don't have enough balance!");
        enterpriseList[msg.sender].balance -= int(_value);
        createTransaction(msg.sender, _to, true, false, 7 days, _value);
        return true;
    }

    function payByRe(address _to, uint _value) public returns(bool){
        require(enterpriseList[msg.sender].isValid, "Your enterprise hasn't been registered yet!");
        require(enterpriseList[_to].isValid, "Target enterprise hasn't been registered yet!");
        require(msg.sender != _to, "You can't trade with yourself!");
        require(_value > 0, "The amount paid should be greater than zero!");
        require(enterpriseList[msg.sender].receivalblesAmount >= _value, "You don't have enough receivalbles!");
        enterpriseList[msg.sender].receivalblesAmount -= _value;
        createTransaction(msg.sender, _to, false, true, 7 days, _value);
        return true;
    }

    function payByNewRe(address _to, uint _value) public returns(bool){
        require(enterpriseList[msg.sender].isValid, "Your enterprise hasn't been registered yet!");
        require(enterpriseList[_to].isValid, "Target enterprise hasn't been registered yet!");
        require(msg.sender != _to, "You can't trade with yourself!");
        require(_value > 0, "The amount paid should be greater than zero!");
        createTransaction(msg.sender, _to, false, false, 7 days, _value);
        return true;
    }

    function loan(address _bank, uint _value) public returns(bool){
        require(enterpriseList[msg.sender].isValid, "Your enterprise hasn't been registered yet!");
        require(enterpriseList[_bank].isValid, "The bank enterprise hasn't been registered yet!");
        require(enterpriseList[_bank].isBank, "The target enterprise should be a bank!");
        require(_value > 0, "The amount should be greater than zero!");
        require(enterpriseList[msg.sender].receivalblesAmount >= _value, "You don't have enough receivalbles!");
        enterpriseList[msg.sender].receivalblesAmount -= _value;
        createTransaction(msg.sender, _bank, false, true, 7 days, _value);
        return true;
    }
    function acceptTrans(uint _index) public returns (bool){
        require(enterpriseList[msg.sender].isValid, "Your enterprise hasn't been registered yet!");
        require(msg.sender == transactionsList[_index].recevier, "You're not the seller of this deal.");
        require(transactionsList[_index].endTime >= now, "The transaction is overdue!");
        require(transactionsList[_index].isValid, "The transaction is invalid!");
        address _from = transactionsList[_index].sender;
        if(transactionsList[_index].isUseBalance){
            enterpriseList[msg.sender].balance += int(transactionsList[_index].value);
        }
        else if(transactionsList[_index].isTransferRe){
            uint sum = transactionsList[_index].value;
            for(uint i = 0; i < enterpriseList[_from].receivalblesNum && sum > 0; i++){
                uint index = enterpriseList[_from].receivalblesIndex[i];
                if(debtsList[index].value >= sum){
                    transferRe(index, msg.sender, sum);
                    sum = 0;
                }
                else{
                    transferRe(index, msg.sender, debtsList[index].value);
                    sum -= debtsList[index].value;
                }
            }
            if(enterpriseList[msg.sender].isBank){
                enterpriseList[_from].balance += int(transactionsList[_index].value);
            }
        }
        else{
            createDebt(_from, msg.sender, transactionsList[_index].value, now + 30 days);
        }
        delTransactionIndex(_index, msg.sender);
        delTransactionIndex(_index, _from);
        transactionsList[_index].isValid = false;
        return true;
    }

    function cancelTrans(uint _index) public returns(bool){
        require(enterpriseList[msg.sender].isValid, "Your enterprise hasn't been registered yet!");
        require(msg.sender == transactionsList[_index].sender, "You're not the originator of this deal.");
        require(transactionsList[_index].isValid, "The transaction is invalid!");
        transactionCancel(_index);
        return true;
    }

    function refuseTrans(uint _index) public returns(bool){
        require(enterpriseList[msg.sender].isValid, "Your enterprise hasn't been registered yet!");
        require(msg.sender == transactionsList[_index].recevier, "You're not the seller of this deal.");
        require(transactionsList[_index].isValid, "The transaction is invalid!");
        transactionCancel(_index);
        return true;
    }

    function payForDebt(uint _index) public returns(bool){
        require(enterpriseList[msg.sender].isValid, "Your enterprise hasn't been registered yet!");
        require(msg.sender == debtsList[_index].from, "You're not the debtor of this debt");
        require(debtsList[_index].isValid, "The debt is valid");
        require(enterpriseList[msg.sender].balance >= int(debtsList[_index].value), "You don't have enough balance!");
        enterpriseList[debtsList[_index].from].balance -= int(debtsList[_index].value);
        enterpriseList[debtsList[_index].to].balance += int(debtsList[_index].value);
        delDebt(_index);
        return true;
    }

    function askPayForDebt(uint _index) public returns(bool){
        require(enterpriseList[msg.sender].isValid, "Your enterprise hasn't been registered yet!");
        require(msg.sender == debtsList[_index].to, "You're not the creditor of this debt");
        require(debtsList[_index].isValid, "The debt is valid");
        require(debtsList[_index].endTime <= now, "The time of the bond has not expired");
        enterpriseList[debtsList[_index].from].balance -= int(debtsList[_index].value);
        enterpriseList[debtsList[_index].to].balance += int(debtsList[_index].value);
        delDebt(_index);
        return true;
    }

    function getDebtsIndex() public view returns(uint[] memory){
        require(enterpriseList[msg.sender].isValid, "Your enterprise has been registered!");
        uint[] memory res = new uint[](enterpriseList[msg.sender].debtsNum);
        for(uint i = 0; i < res.length; i++){
            res[i] = enterpriseList[msg.sender].debtsIndex[i];
        }
        return res;
    }

    function getDebtsNum() public view returns(uint){
        require(enterpriseList[msg.sender].isValid, "Your enterprise has been registered!");
        return enterpriseList[msg.sender].debtsNum;
    }


    function getReIndex() public view returns(uint[] memory){
        require(enterpriseList[msg.sender].isValid, "Your enterprise has been registered!");
        uint[] memory res = new uint[](enterpriseList[msg.sender].receivalblesNum);
        for(uint i = 0; i < res.length; i++){
            res[i] = enterpriseList[msg.sender].receivalblesIndex[i];
        }
        return res;
    }

    function getReNum() public view returns(uint){
        require(enterpriseList[msg.sender].isValid, "Your enterprise has been registered!");
        return enterpriseList[msg.sender].receivalblesNum;
    }

    function getTransIndex() public view returns(uint[] memory){
        require(enterpriseList[msg.sender].isValid, "Your enterprise has been registered!");
        uint[] memory res = new uint[](enterpriseList[msg.sender].transactionNum);
        for(uint i = 0; i < res.length; i++){
            res[i] = enterpriseList[msg.sender].transactionsIndex[i];
        }
        return res;
    }

    function getTransNum() public view returns(uint){
        require(enterpriseList[msg.sender].isValid, "Your enterprise has been registered!");
        return enterpriseList[msg.sender].transactionNum;
    }

    function getReAmount() public view returns (uint){
        require(enterpriseList[msg.sender].isValid, "Your enterprise hasn't been registered yet!");
        return enterpriseList[msg.sender].receivalblesAmount;
    }

    function getDebtsAmount() public view returns (uint){
        require(enterpriseList[msg.sender].isValid, "Your enterprise hasn't been registered yet!");
        return enterpriseList[msg.sender].debtsAmount;
    }

    function addTransaction(uint id, address _to) private{
        enterpriseList[_to].transactionsIndex[enterpriseList[_to].transactionNum] = id;
        enterpriseList[_to].transactionNum++;
    }
    function createTransaction(address _from, address _to, bool _isUseBalance, bool _isTransferRe, uint _lastTime, uint _value)
                private returns (uint){
        transactionsList[transactionsId] = Transaction(true, now + _lastTime, _isUseBalance, _isTransferRe, _from, getName(_from), _to, getName(_to), _value);
        addTransaction(transactionsId, _from);
        addTransaction(transactionsId, _to);
        transactionsId++;
        return transactionsId-1;
    }

    function delDebt(uint _index) private{
        require(debtsList[_index].isValid == true, "This Debt has been invalid yet");
        address _from = debtsList[_index].from;
        address _to = debtsList[_index].to;
        uint i = 0;
        uint j = 0;
        for(i = 0; i < enterpriseList[_from].debtsNum; i++){
            if(enterpriseList[_from].debtsIndex[i] == _index){
                for(j = i; j < enterpriseList[_from].debtsNum-1; j++){
                    enterpriseList[_from].debtsIndex[j] = enterpriseList[_from].debtsIndex[j + 1];
                }
                break;
            }
        }
        enterpriseList[_from].debtsNum--;
        enterpriseList[_from].debtsAmount -= debtsList[_index].value;
        for(i = 0; i < enterpriseList[_to].receivalblesNum; i++){
            if(enterpriseList[_to].receivalblesIndex[i] == _index){
                for(j = i; j < enterpriseList[_to].receivalblesNum - 1 ; j++){
                    enterpriseList[_to].receivalblesIndex[j] = enterpriseList[_to].receivalblesIndex[j + 1];
                }
                break;
            }
        }
        enterpriseList[_to].receivalblesNum--;
        enterpriseList[_to].receivalblesAmount -= debtsList[_index].value;
        debtsList[_index].isValid = false;
    }

    function addDebt(address _u, uint _index) private{
        enterpriseList[_u].debtsIndex[enterpriseList[_u].debtsNum] = _index;
        enterpriseList[_u].debtsNum++;
        enterpriseList[_u].debtsAmount += debtsList[_index].value;
    }
    function addRe(address _u, uint _index) private{
        enterpriseList[_u].receivalblesIndex[enterpriseList[_u].receivalblesNum] = _index;
        enterpriseList[_u].receivalblesNum++;
        enterpriseList[_u].receivalblesAmount += debtsList[_index].value;
    }

    function createDebt(address _from, address _to, uint _value, uint endTime) private {
        debtsList[debtsId] = Debt(true, endTime, _from, _to, _value);
        addDebt(_from, debtsId);
        addRe(_to, debtsId);
        debtsId++;
    }

    function transferRe(uint _index, address _to, uint _sum) private{
        require(debtsList[_index].isValid, "The debt is invalid!");
        address ori = debtsList[_index].from;
        if(ori != _to){
            createDebt(ori, _to, _sum, debtsList[_index].endTime);
        }
        enterpriseList[ori].debtsAmount -= _sum;
        debtsList[_index].value -= _sum;
        if(debtsList[_index].value == 0){
            delDebt(_index);
        }
    }

    function delTransactionIndex(uint _index, address _u) private{
        for(uint i = 0; i < enterpriseList[_u].transactionNum; i++){
            if(enterpriseList[_u].transactionsIndex[i] == _index){
                for(uint j = i; j < enterpriseList[_u].transactionNum - 1; j++){
                    enterpriseList[_u].transactionsIndex[j] = enterpriseList[_u].transactionsIndex[j+1];
                }
                enterpriseList[_u].transactionNum--;
                break;
            }
        }
    }

    function transactionCancel(uint _index) private{
        require(transactionsList[_index].isValid, "The transaction is invalid!");
        address _from = transactionsList[_index].sender;
        address _to = transactionsList[_index].recevier;
        if(transactionsList[_index].isUseBalance){
            enterpriseList[_from].balance += int(transactionsList[_index].value);
        }
        else if(transactionsList[_index].isTransferRe){
            enterpriseList[_from].receivalblesAmount += transactionsList[_index].value;
        }
        delTransactionIndex(_index, _from);
        delTransactionIndex(_index, _to);
        transactionsList[_index].isValid = false;
    }

}
