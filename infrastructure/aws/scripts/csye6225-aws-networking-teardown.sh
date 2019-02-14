##delete subnet
##delete route table

##detach gateway
##delete gate way
##delete vpc
set -e
aws ec2 describe-vpcs
echo "Please input the vpc id:"
read name
route_table=$( aws ec2 describe-route-tables --filters Name=vpc-id,Values=$name )

tables=$( echo "$route_table" | jq '.RouteTables' | jq '.[0]' )
# echo $tables
associations=$( echo "$tables" | jq '.Associations' | jq '.[0]')
# echo $associations
subnet_id=$( echo "$associations" | jq '.SubnetId' | sed 's/\"//g' )
# echo $subnet_id
subnetId_length=$( echo "$associations" | jq '.SubnetId | length')
# echo $subnetId_length

while [ $subnetId_length -eq 0 ]; do
	echo "chagne table"
	tables=$( echo "$route_table" | jq '.RouteTables' | jq '.[1]' )
	associations=$( echo "$tables" | jq '.Associations' | jq '.[0]')
	subnet_id=$( echo "$associations" | jq '.SubnetId' | sed 's/\"//g' )
	subnetId_length=$( echo "$associations" | jq '.SubnetId | length')
	break
done
# echo $subnet_id

if [ "$subnetId_length" -eq 0 ]; then
	echo "Wrong Information!"
	exit 0
fi

associations1=$( echo "$tables" | jq '.Associations' | jq '.[1]' )
# # echo $associations1
subnet_id1=$( echo "$associations1" | jq '.SubnetId' | sed 's/\"//g' )
# echo $subnet_id1

associations2=$( echo "$tables" | jq '.Associations' | jq '.[2]' )
# # echo $associations2
subnet_id2=$( echo "$associations2" | jq '.SubnetId' | sed 's/\"//g' )
# echo $subnet_id2

route_table_id=$( echo "$associations" | jq '.RouteTableId' | sed 's/\"//g' )
# echo $route_table_id

routes=$( echo "$tables" | jq '.Routes' | jq '.[1]' )
# echo $routes
gateway_id=$( echo "$routes" | jq '.GatewayId' | sed 's/\"//g' )
# echo $gateway_id

aws ec2 delete-subnet --subnet-id $subnet_id
echo " Delete Subnet1..." $subnet_id
aws ec2 delete-subnet --subnet-id $subnet_id1
echo " Delete Subnet2..." $subnet_id1
aws ec2 delete-subnet --subnet-id $subnet_id2
echo " Delete Subnet3..." $subnet_id2
aws ec2 delete-route-table --route-table-id $route_table_id
echo " Delete Route Table..." $route_table_id
aws ec2 detach-internet-gateway --internet-gateway-id $gateway_id --vpc-id $name
echo " Detach Internet GateWay..." $gateway_id
aws ec2 delete-internet-gateway --internet-gateway-id $gateway_id
echo " Delete Internet Gateway..." $gateway_id
aws ec2 delete-vpc --vpc-id $name
echo " Delete Vpc..." $name