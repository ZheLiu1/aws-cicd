#!/bin/bash
echo "Please enter CircleCI Stack Name:"
read appStackName
if [ -z "$appStackName" ]
then
	echo "StackName error exiting!"
	exit 1
fi

echo "Validating template"
RC=$(aws cloudformation validate-template --template-body file://./csye6225-cf-circleci.json)
echo "Template is valid"

if [ $? -eq 0 ]
then
	echo "Success: validate template"
else
	echo "Fail validate template"
	exit 1
fi

# Domain name for ARN
DOMAIN_NAME=$(aws route53 list-hosted-zones --query HostedZones[0].Name --output text)

CD_DOMAIN="code-deploy."${DOMAIN_NAME%?}


# Account id for arn
echo "Fetching user's account id"
ACCOUNT_ID=$(aws sts get-caller-identity --query 'Account' --output text)

RC=$(aws cloudformation create-stack --stack-name $appStackName --capabilities "CAPABILITY_NAMED_IAM" --template-body file://./csye6225-cf-circleci.json --parameters ParameterKey=CDARN,ParameterValue=arn:aws:s3:::$CD_DOMAIN/* ParameterKey=CDAPPNAME,ParameterValue=csye6225-webapp)

echo "CI stack creation in progress. Please wait"
aws cloudformation wait stack-create-complete --stack-name $appStackName
STACKDETAILS=$(aws cloudformation describe-stacks --stack-name $appStackName --query Stacks[0].StackId --output text)
echo "CI stack creation complete"
echo "CI Stack id: $STACKDETAILS"
