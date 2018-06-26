using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;

namespace AdmPanel.Models
{
    public class Appeal
    {
        public int Id { get; set; }
        public string AppealAuthorName { get; set; }
        public bool AppealToUser { get; set; }
        public int AppealPostOrUserId { get; set; }
        public string AppealMessage { get; set; }
        public string AppealDate { get; set; }
        public bool AppealStatus { get; set; }
    }
}