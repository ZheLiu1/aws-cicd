echo "Enter your network stack name"
read networkStackName
if [ -z "$networkStackName" ]
then
	echo "StackName is incorrect!"
	exit 1
fi
#echo "$networkStackName"



echo "Enter your application stack name"
read appStackName
if [ -z "$appStackName" ]
then
	echo "StackName is incorrect!"
	exit 1
fi
#echo "$appStackName"



echo "Please enter an active keyPair to associate with EC2:"
read keyName
if [ -z "$keyName" ]
then
	echo "StackName is incorrect!"
	exit 1
fi
#echo "$keyName"

echo "Please enter the ImageID of centos AMI  created"
read imageid
if [ -z "$imageid" ]
then
	echo "ImageID is incorrect!"
	exit 1
fi

VpcId=$(aws ec2 describe-vpcs --query 'Vpcs[].{VpcId:VpcId}' \
--filters "Name=tag:Name,Values=$networkStackName-csye6225-vpc" "Name=is-default, Values=false" --output text 2>&1)


subnet=$(aws ec2 describe-subnets --filters Name=vpc-id,Values=${VpcId})
subnetid1=$(echo -e "$subnet" | jq '.Subnets[0].SubnetId' | tr -d '"')

subnetid2=$(echo -e "$subnet" | jq '.Subnets[1].SubnetId' | tr -d '"')

subnetid3=$(echo -e "$subnet" | jq '.Subnets[2].SubnetId' | tr -d '"')





if [ -z "$VpcId" ]
then
	echo "VPC ID is incorrect!"
	exit 1
fi

if [ -z "$subnetid1" ]
then
	echo "Subnet ID 1 is incorrect!"
	exit 1
fi

if [ -z "$subnetid2" ]
then
	echo "Subnet ID 2 is incorrect!"
	exit 1
fi


if [ -z "$subnetid3" ]
then
	echo "Subnet ID 3 is incorrect!"
	exit 1
fi


echo "Wait while your template is being validated............"
TMP_code=`aws cloudformation validate-template --template-body file://./csye6225-cf-application.json`
if [ -z "$TMP_code" ]
then
	echo "Template is incorrect!"
	exit 1
fi
echo "Your template validation was successful!"

echo "Wait while your CloudFormation Stack is being created..............."

CRTSTACK_Code=`aws cloudformation create-stack --stack-name $appStackName --template-body file://./csye6225-cf-application.json --parameters ParameterKey=NetworkStackNameParameter,ParameterValue=$networkStackName ParameterKey=ApplicationStackNameParameter,ParameterValue=$appStackName ParameterKey=KeyName,ParameterValue=$keyName ParameterKey=VpcID,ParameterValue=$VpcId ParameterKey=PublicSubnetKey1,ParameterValue=$subnetid1 ParameterKey=PublicSubnetKey2,ParameterValue=$subnetid2 ParameterKey=PublicSubnetKey3,ParameterValue=$subnetid3 ParameterKey=ImageID,ParameterValue=$imageid`
if [ -z "$CRTSTACK_Code" ]
then
	echo "Stack Creation is incorrect!"
	exit 1
fi
aws cloudformation wait stack-create-complete --stack-name $appStackName
echo "Your application stack is successfully Created."
