using AdmPanel.Models;
using RestSharp;
using Retrofit.Net.Attributes.Methods;
using Retrofit.Net.Attributes.Parameters;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace AdmPanel.Utils
{
    public interface IMonitoringService
    {
        [Get("/admin")]
        RestResponse<List<int>> GetSocialInfo();

        [Get("/admin/{postId}")]
        RestResponse<Post> GetPost([Path("postId")] int postId);

        [Get("/post/del/{id}")]
        RestResponse<int> DeletePost([Path("id")] int id);
    }
}
