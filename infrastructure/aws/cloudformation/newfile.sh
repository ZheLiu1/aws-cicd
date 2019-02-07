echo "Enter Stack Name"
read StackName
echo "Validating template"
RC=$(aws cloudformation validate-template --template-body file://./csye6225-cf-networking.json)
if [ $? -eq 0 ]
then
	echo "The Template is Correct!!"
else
	echo "Invalid Template. Please keep a well formatted template!!"
	exit 1
fi
RC1=$(aws cloudformation create-stack --stack-name $StackName --template-body file://./csye6225-cf-networking.json --parameters ParameterKey=VPCNAME,ParameterValue=$StackName-csye6225-vpc ParameterKey=IGWNAME,ParameterValue=$StackName-csye6225-IG ParameterKey=PUBLICROUTETABLENAME,ParameterValue=$StackName-csye6225-rt)
if [ $? -eq 0 ]
then
	echo "Started.......................   cloud formation..."
else
	echo "Stack Formation Failed. Please restart or change the Json!!"
	exit 1
fi

echo "Stack creation in progress. Please wait"
aws cloudformation wait stack-create-complete --stack-name $StackName
STACKDETAILS=$(aws cloudformation describe-stacks --stack-name $StackName --query Stacks[0].StackId --output text)
if [ $? -eq 0 ]
then
	echo "Stack has been created with no errors."
else
	echo "Something went wrong"
	exit 1
fi


