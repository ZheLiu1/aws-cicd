echo "Enter Stack Name"
read StackName
RC=$(aws cloudformation describe-stacks --stack-name $StackName --query Stacks[0].StackId --output text)

if [ $? -eq 0 ]
then
	echo "Deleting the Stack"
else
	echo "Stack '$StackName' doesn't exist"
	exit 1
fi

RC1=$(aws cloudformation delete-stack --stack-name $StackName)
RC2=$(aws cloudformation wait stack-delete-complete --stack-name $StackName)

if [ $? -eq 0 ]
then
	echo "Stack Deleted"
else
	echo "Something Went Wrong"
	exit 1
fi
