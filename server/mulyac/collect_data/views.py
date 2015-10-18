from django.http import HttpResponse
from django.shortcuts import render, render_to_response
from django.views.decorators.csrf import csrf_exempt
from django.views.decorators.http import require_GET
from django.template import loader, Context
from mulyac import settings
import simplejson as json
import os

LOG_FILE_NAME = os.path.dirname(os.path.realpath(__file__))+"/../pansLog.txt"

# Create your views here.
@csrf_exempt
def usage(request):
    settings.logger.info(json.dumps(request.POST.dict()))
    return HttpResponse("OK")


@require_GET
def show(request): 
    #user_id = request.GET.get('id_user', 'abcd')
    user_id = "kbm1378"
    usages = []
    usages.extend(get_usage(user_id))
    return render(request, "show.html", {'usages':usages})



def get_usage(user_id):
    if not does_logfile_exist():
        return []

    log = open(LOG_FILE_NAME, 'r')
    list_events = []
    while 1:
        line = log.readline()
        if not line:
            break
        event = json.loads(line)
        list_events.append(event)
    return get_events(user_id, list_events)


def does_logfile_exist():
    return os.path.exists(LOG_FILE_NAME)




def get_events(user_id, list_events):
    events = []

    for event in list_events:
        if 'id' in event and event['id'] == user_id:
     		if 'status' in event and int(event['status']) >0: 
	   		events.append(1)
    return events
