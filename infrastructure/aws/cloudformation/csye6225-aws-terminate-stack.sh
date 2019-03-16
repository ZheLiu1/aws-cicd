# Delete CloudFormation Stack
echo "CloudFormation Stack Name:"
read name
echo "Deleting CloudFormation Stack"
aws cloudformation delete-stack --stack-name $name

aws cloudformation wait stack-delete-complete --stack-name $name

echo "Cloudformation Stack deleted"
