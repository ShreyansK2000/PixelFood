"""class SaveSnapshots(APIView):
    def handlefileupload(self, ufiles):
        for ufile in ufiles:
            with open("images/"+ufile.name, 'wb+') as destination:
                for chunk in ufile.chunks():
                    destination.write(chunk)

    def post(self,request, *args, **kwargs):
        self.handlefileupload(request.FILES.getlist('file        return Response({"status":"success"})'))
"""