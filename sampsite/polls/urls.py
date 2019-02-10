from django.urls import path, re_path

from . import views

app_name = 'polls'

urlpatterns = [
    path('', views.index, name='index'),
    re_path(r'^(?P<question_id>[0-9]+)/$', views.detail, name='detail'),
    re_path(r'^(?P<question_id>[0-9]+)/results/', views.results, name='results'),
    re_path(r'^(?P<question_id>[0-9]+)/vote/', views.vote, name='vote'),
]
