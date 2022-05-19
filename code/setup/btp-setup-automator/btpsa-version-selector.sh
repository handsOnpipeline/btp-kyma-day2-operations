#!/usr/bin/env bash

log() {
  # Print the input text in yellow.
  local yellow='\033[0;33m'
  local no_color='\033[0m'
  echo -e "${yellow}$*${no_color}"
}

VARIANT_NUMBER=1
COLUMNS=12
# for returning a single value:
declare retval=""

# user selection via passed array
# return ( index value)
createmenu() {
  #echo "Size of array: $#"
  #echo "$@"
  select option; do # in "$@" is the default
    if [ "$REPLY" -eq "$#" ];
    then
      #echo "Exiting..."
      break;
    elif [ 1 -le "$REPLY" ] && [ "$REPLY" -le $(($#-1)) ];
    then
      #echo "You selected $option which is option $REPLY"
      break;
    else
      echo "Incorrect Input: Select a number 1-$#"
    fi
  done
  retval=$REPLY  
}

log "Choose Deployment option, Variant 1 will deploy the application ready for the Mission. Variant 2 contains all the enhancements which will be introduced throughout the mission:"
declare -a arr="(Mission-Start Mission-End)"    # must be quoted like this
createmenu "${arr[@]}"
VARIANT_NUMBER="$retval"

case $VARIANT_NUMBER in
  1)	
    BTPSA_KYMA_IMAGE_TAG="main"
  ;;&    
  2)
    BTPSA_KYMA_IMAGE_TAG="final"
  ;;          
esac

log "Deployment will be performed from branch $BTPSA_KYMA_IMAGE_TAG"

