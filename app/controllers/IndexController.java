package controllers;

import com.theoryinpractise.halbuilder5.ResourceRepresentation;
import com.theoryinpractise.halbuilder5.json.JsonRepresentationWriter;
import play.mvc.Controller;
import play.mvc.Result;
import utils.HalLinks;
import utils.HalUtils;

import static utils.HalLinks.favorites;

public class IndexController extends Controller {

    public Result index() {
        ResourceRepresentation<Void> representation =
                ResourceRepresentation.empty()
                    .withLink(favorites());

        return HalUtils.toJsonResult(OK, representation);
    }
}
