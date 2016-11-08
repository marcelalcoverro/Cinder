

#include "cinder/android/renderer.h"

#include "fezoolib/Core/DepthGeometry.hpp"




// We'll create a new Cinder Application by deriving from the App class.
class CustomRender : public Renderer {
  public:
	CustomRender()
	{
		Renderer::setRenderer(this);
	}

};


static CustomRender myRenderer;

